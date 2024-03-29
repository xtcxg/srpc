package com.miex.registry.redis;

import com.miex.config.RegistryConfig;
import com.miex.config.ExchangeConfig;
import com.miex.exception.SrpcException;
import com.miex.exchange.ExchangeManager;
import com.miex.protocol.ProtocolManager;
import com.miex.registry.Registry;
import com.miex.registry.RegistryManager;
import com.miex.util.CollectionUtil;
import com.miex.util.StringUtil;
import java.time.Duration;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executor;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;
import redis.clients.jedis.Transaction;
import redis.clients.jedis.params.SetParams;

public class RedisRegistry implements Registry {

    private static final Log log = LogFactory.getLog(RedisRegistry.class);

    private static ConcurrentHashMap<String, List<String>> REGISTRY_INFO = new ConcurrentHashMap<>();

    private static final RegistryConfig REGISTRY_CONFIG = RegistryManager.getRegistryConfig();
    private static final ExchangeConfig SERVER_CONFIG = ExchangeManager.getExchangeConfig();

    // 需要保持心跳，时间设置为无限
    private static final Executor pool = new ThreadPoolExecutor(8, 8, Integer.MAX_VALUE, TimeUnit.DAYS, new LinkedBlockingQueue<>());

    // 当前服务对外提供的接口
    private static List<String> LOCAL_SERVICE;

    // 注册中心所有的服务
    private static final ConcurrentHashMap<String, List<String>> REGISTRY_SERVICE = new ConcurrentHashMap<>();

    // 服务状态
    private static Boolean ALIVE = true;
    // 当前服务是否占有锁
    // todo 去掉锁，只使用STATE
    private static Boolean LOCKED = false;
    // 当前服务占有锁的数量
    private static int STATE = 0;

    private String address;

    private JedisPool jedisPool;

    @Override
    public void connect(String host, Integer port, String name, String password) {
        Integer serverPort = SERVER_CONFIG.getPort();
        this.address = SERVER_CONFIG.getHost() + ":" + serverPort;
        JedisPoolConfig config = new JedisPoolConfig();
        config.setMaxTotal(100);
        config.setMaxIdle(50);
        config.setMinIdle(5);
        config.setMaxWait(Duration.ofSeconds(2));
        config.setTimeBetweenEvictionRuns(Duration.ofSeconds(15));
        jedisPool = new JedisPool(config, host, port);
        Jedis jedis = jedisPool.getResource();
        jedis.connect();
        if (!"PONG".equals(jedis.ping())) {
            throw new SrpcException(SrpcException.Enum.REGISTRY_CONNECT_FAIL, "registry fail [" + host + ":" + port + "]");
        }
        jedisPool.returnResource(jedis);
    }

    @Override
    public void register() {
        Jedis jedis = jedisPool.getResource();
        // 本地数据
        LOCAL_SERVICE = ProtocolManager.getInstance().getLocalKeys().stream().map(Class::getName)
            .collect(Collectors.toList());
        jedis.hset(REGISTRY_CONFIG.getHostIndexName(), this.address, LOCAL_SERVICE.toString());
        jedis.setex(address, REGISTRY_CONFIG.getTtl(), LOCAL_SERVICE.toString());
        jedisPool.returnResource(jedis);
        refresh();
        lock();
        // 注册中心数据
        REGISTRY_INFO = pull();
        // 注册中心的数据
        Map<String, List<String>> mergeService = merge(LOCAL_SERVICE, REGISTRY_INFO, this.address);
        push(mergeService);
        unlock();
        keepAlive();
    }

    /**
     * 将注册中心中无效的数据去除，并更新本地数据
     */
    @Override
    public void refresh() {
        Jedis jedis = jedisPool.getResource();
        long start = System.currentTimeMillis();
        lock();
        Set<String> members = jedis.hkeys(REGISTRY_CONFIG.getHostIndexName());
//        Map<String, List<String>> registryService = new HashMap<>();
        REGISTRY_SERVICE.clear();
        for (String member : members) {
            if (StringUtil.isEmpty(jedis.get(member))) {
                // 清除下线的服务
                jedis.hdel(REGISTRY_CONFIG.getHostIndexName(), member);
            } else {
                String serverInfo = jedis.hget(REGISTRY_CONFIG.getHostIndexName(), member);
                List<String> targetService = CollectionUtil.toList(serverInfo);
                merge(targetService, REGISTRY_SERVICE, member);
            }
        }
        jedis.del(REGISTRY_CONFIG.getServerIndexName());
        push(REGISTRY_SERVICE);
        ExchangeManager.getInstance().syncServer(REGISTRY_SERVICE);
        unlock();
        log.debug("refresh used " + (System.currentTimeMillis() - start) + "ms");
    }

    /**
     * 将指定的服务数据与服务器数据合并
     *
     * @param targetService   指定的服务数据
     * @param registryService 服务器服务数据
     * @param addr            指定服务所在的地址
     * @return 合并结果
     */
    private Map<String, List<String>> merge(List<String> targetService, Map<String,
        List<String>> registryService, String addr) {
        for (String name : targetService) {
            registryService.merge(name, new ArrayList<>() {{
                add(addr);
            }}, (o, n) -> {
                if (o.stream().noneMatch(e -> e.equals(addr))) {
                    o.add(addr);
                }
                return o;
            });
        }
        return registryService;
    }

    /**
     * 将指定的服务数据从服务器数据中剔除
     *
     * @param targetService   指定的服务数据
     * @param registryService 服务器服务数据
     * @param address         指定服务所在的地址
     * @return 剔除结果
     */
    private Map<String, List<String>> exclude(List<String> targetService,
        Map<String, List<String>> registryService, String address) {
        for (String name : targetService) {
            List<String> list = registryService.get(name);
            if (null != list) {
                list.remove(address);
            }
        }
        return registryService;
    }

    @Override
    public void clean() {
        if (!lock(5000L)) {
            log.error("can't acquire lock to use registry");
            throw new SrpcException(SrpcException.Enum.REGISTRY_LOCK_FAIL);
        }
        Jedis jedis = jedisPool.getResource();
        jedis.hdel(REGISTRY_CONFIG.getHostIndexName(), address);
        jedis.del(address);
        jedisPool.returnResource(jedis);
        Map<String, List<String>> registryService = pull();
        exclude(LOCAL_SERVICE, registryService, this.address);
        push(registryService);
        unlock();
    }

    @Override
    public ConcurrentHashMap<String, List<String>> pull(String[] names) {
        Jedis jedis = jedisPool.getResource();
        if (names.length == 0) {
            return new ConcurrentHashMap<>();
        }
        if (names[0].equals("*")) {
            REGISTRY_SERVICE.clear();
            jedis.hgetAll(REGISTRY_CONFIG.getServerIndexName());
            Map<String, String> all = jedis.hgetAll(REGISTRY_CONFIG.getServerIndexName());
            for (int i = 0; i < LOCAL_SERVICE.size(); i++) {
                List<String> list = CollectionUtil.toList(all.get(LOCAL_SERVICE.get(i)));
                REGISTRY_SERVICE.put(LOCAL_SERVICE.get(i), list);
            }
            return REGISTRY_SERVICE;
        }
        List<String> hosts = jedis.hmget(REGISTRY_CONFIG.getServerIndexName(), names);
        for (int i = 0; i < names.length; i++) {
            REGISTRY_SERVICE.put(names[i], CollectionUtil.toList(hosts.get(i)));
        }
        jedisPool.returnResource(jedis);
        return REGISTRY_SERVICE;
    }

    public ConcurrentHashMap<String, List<String>> pull() {
        return pull(LOCAL_SERVICE.toArray(new String[0]));
    }

    private void push(Map<String, List<String>> serviceInfo) {
        if (CollectionUtil.isEmpty(serviceInfo.keySet())) {
            return;
        }
        Jedis jedis = jedisPool.getResource();
        if (!lock(5000L)) {
            log.error("can't acquire lock to use registry");
            throw new SrpcException(SrpcException.Enum.REGISTRY_LOCK_FAIL);
        }
        Transaction multi = jedis.multi();
        try {
            List<String> delField = new ArrayList<>();
            Map<String, String> updateField = new HashMap<>();
            for (Map.Entry<String, List<String>> entry : serviceInfo.entrySet()) {
                if (entry.getValue() == null || entry.getValue().isEmpty()) {
                    delField.add(entry.getKey());
                } else {
                    updateField.put(entry.getKey(), entry.getValue().toString());
                }
            }
            if (!delField.isEmpty()) {
                multi.hdel(REGISTRY_CONFIG.getServerIndexName(), delField.toArray(new String[0]));
            }
            if (!updateField.isEmpty()) {
                multi.hmset(REGISTRY_CONFIG.getServerIndexName(), updateField);
            }
            multi.exec();
        } catch (Exception e) {
            log.error("push service info error", e);
            multi.discard();
        }
        unlock();
    }

    @Override
    public void destroy() {
        ALIVE = false;
        clean();
        unlock(); // 冗余操作
        jedisPool.destroy();
    }

    private void keepAlive() {
        Jedis jedis = jedisPool.getResource();
        CompletableFuture.supplyAsync(() -> {
            long time = REGISTRY_CONFIG.getTtl();
            while (ALIVE) {
                try {
                    log.debug("redis registry heart beat:" + address);
                    jedis.setex(address, time, LOCAL_SERVICE.toString());
//                    jedisPool.returnResource(jedis);
                    Thread.sleep(10000);
                } catch (InterruptedException e) {
                    log.error("keepAlive error", e);
                }
            }
            return null;
        }, pool);
    }

    /**
     * 在一定时间内获取锁
     *
     * @param times 尝试时间/ms
     * @return 加锁结果
     */
    private boolean lock(Long times) {
        if (null == times || 100 > times) {
            log.error("RedisRegistry try lock time must bigger than 100ms,at present is [" + times + "]");
            throw new SrpcException(SrpcException.Enum.PARAM_ERROR, "RedisRegistry try lock time must bigger than 100ms,at present is [" + times + "]");
        }
        long interval = 0;

        while (times > 0) {
            try {
                interval = interval < 100 ? interval + 20 : interval;
                Thread.sleep(interval);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            if (lock()) {
                return true;
            }
            times = times - interval;
        }
        return lock();
    }

    private boolean lock() {
        // todo 使用脚本保证原子性
        Jedis jedis = jedisPool.getResource();
        String owner = jedis.get(REGISTRY_CONFIG.getLock());
        jedisPool.returnResource(jedis);
        if (StringUtil.isEmpty(owner) || address.equals(owner)) {
            // 如果上锁成功，保持锁
            STATE++;
            if (STATE > 1) {
                // 如果已经持有锁，不进行后续操作
                return true;
            }
            LOCKED = true;
            CompletableFuture.supplyAsync(() -> {
                SetParams pn = SetParams.setParams().ex(3);
                while (LOCKED) {
                    try {
                        Jedis j = jedisPool.getResource();
                        j.set(REGISTRY_CONFIG.getLock(), address, pn);
                        jedisPool.returnResource(j);
                        Thread.sleep(2000);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
                return null;
            }, pool);
            return true;
        } else {
            // 上锁失败，返回失败
            LOCKED = false;
            return false;
        }

    }

    private void unlock() {
        if (STATE > 0)
            STATE--;
        if (STATE == 0) {
            LOCKED = false;
            jedisPool.getResource().eval("local sip = redis.call(\"get\",KEYS[1]);local lip = ARGV[1];if lip == sip then redis.call(\"del\",KEYS[1]);end;return \"OK\"", 1, REGISTRY_CONFIG.getLock(), address);
        }
    }

    @Override
    public List<String> getHosts(String className) {
        List<String> hosts = REGISTRY_INFO.get(className);
        if (null != hosts) {
            return hosts;
        }
        ConcurrentHashMap<String, List<String>> latest = pull(new String[]{className});
        if (null == latest || latest.get(className) == null) {
            throw new SrpcException(SrpcException.Enum.NO_SERVER_AVAILABLE, "can't find server provide service [" + className + "]");
        }
        REGISTRY_INFO.put(className, latest.get(className));
        return latest.get(className);
    }

    @Override
    public ConcurrentHashMap<String, List<String>> getServices() {
        return REGISTRY_SERVICE;
    }
}
