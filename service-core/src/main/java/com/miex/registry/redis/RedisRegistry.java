package com.miex.registry.redis;

import com.miex.cache.CacheManager;
import com.miex.exception.SrpcException;
import com.miex.protocol.DefaultExporter;
import com.miex.registry.Registry;
import com.miex.util.CollectionUtil;
import com.miex.util.StringUtil;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;
import redis.clients.jedis.Transaction;
import redis.clients.jedis.params.SetParams;

import java.net.Inet4Address;
import java.net.UnknownHostException;
import java.util.*;
import java.util.concurrent.*;

public class RedisRegistry implements Registry {

    private static final Log log = LogFactory.getLog(RedisRegistry.class);

    private Jedis jedis;

    private static ConcurrentHashMap<String, List<String>> REGISTRY_INFO = new ConcurrentHashMap<>();

    // 需要保持心跳，时间设置为无限
    private static final Executor pool = new ThreadPoolExecutor(8, 8, Integer.MAX_VALUE, TimeUnit.DAYS, new LinkedBlockingQueue<>());

    // 对外提供的服务
    private static List<String> LOCAL_SERVICE;

    private static final String LOCK_KEY = CacheManager.PROPERTIES_CACHE.get("srpc.registry.redis.lock.key");
    private static final String SERVICE_KEY = CacheManager.PROPERTIES_CACHE.get("srpc.registry.service.key");
    private static final String KEEP_TIME = CacheManager.PROPERTIES_CACHE.get("srpc.registry.server.ttl");
    private static final String HOSTS = CacheManager.PROPERTIES_CACHE.get("srpc.registry.redis.hosts");
    private static Boolean ALIVE = true;
    private static Boolean LOCKED = false;
    private static int STATE = 0;

    private String address;

    private JedisPool jedisPool;

    @Override
    public void connect(String host, Integer port, String name, String password) {
        String serverPort = CacheManager.PROPERTIES_CACHE.get("srpc.port");
        try {
            this.address = Inet4Address.getLocalHost().getHostAddress() + ":" + serverPort;
        } catch (UnknownHostException e) {
            this.address = CacheManager.PROPERTIES_CACHE.get("srpc.ip") + ":" + serverPort;
            if (StringUtil.isEmpty(this.address)) {
                throw new SrpcException(SrpcException.Enum.SYSTEM_ERROR, "can't acquire service's ip,also can set value [srpc.ip]");
            }
        }
        JedisPoolConfig config = new JedisPoolConfig();
        config.setMaxTotal(20);
        config.setMaxIdle(10);
        jedisPool = new JedisPool(config, host, port);
        jedis = jedisPool.getResource();
        jedis.connect();
        if (!"PONG".equals(jedis.ping())) {
            throw new SrpcException(SrpcException.Enum.REGISTRY_CONNECT_FAIL, "registry fail [" + host + ":" + port + "]");
        }
    }

    @Override
    public void register() {
        // 本地数据
        LOCAL_SERVICE = CacheManager.PROVIDE_CACHE.keys();
        keepAlive();
        jedis.hset(HOSTS, this.address, LOCAL_SERVICE.toString());
        refresh();
        lock();
        // 注册中心数据
        REGISTRY_INFO = pull();
        // 注册中心的数据
        Map<String, List<String>> mergeService = merge(LOCAL_SERVICE, REGISTRY_INFO, this.address);
        push(mergeService);
        unlock();
    }

    /**
     * 将注册中心中无效的数据去除，并更新本地数据
     */
    @Override
    public void refresh() {
        Jedis jedis = jedisPool.getResource();
        try {
            long start = System.currentTimeMillis();
            lock();
            Set<String> members = jedis.hkeys(HOSTS);
            Map<String, List<String>> registryService = new HashMap<>();
            for (String member : members) {
                if (StringUtil.isEmpty(jedis.get(member))) {
                    // 清除下线的服务
                    jedis.hdel(HOSTS, member);
                } else {
                    String serverInfo = jedis.hget(HOSTS, member);
                    List<String> targetService = CollectionUtil.toList(serverInfo);
                    merge(targetService, registryService, member);
                }
            }
            jedis.del(SERVICE_KEY);
            push(registryService);
            log.debug("refresh used " + (System.currentTimeMillis() - start) + "ms");
        } catch (Exception e) {
            e.printStackTrace();
        }
        unlock();
    }

    /**
     * 将指定的服务数据与服务器数据合并
     *
     * @param targetService   指定的服务数据
     * @param registryService 服务器服务数据
     * @param addr            指定服务所在的地址
     * @return 合并结果
     */
    private Map<String, List<String>> merge(List<String> targetService, Map<String, List<String>> registryService, String addr) {
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
     * @param addr            指定服务所在的地址
     * @return 剔除结果
     */
    private Map<String, List<String>> exclude(List<String> targetService, Map<String, List<String>> registryService, String addr) {
        for (String name : targetService) {
            List<String> list = registryService.get(name);
            if (null != list) {
                list.remove(addr);
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
        jedis.hdel(HOSTS, address);
        jedis.del(address);
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
        ConcurrentHashMap<String, List<String>> serviceMap = new ConcurrentHashMap<>();
        if (names[0].equals("*")) {
            jedis.hgetAll(SERVICE_KEY);
            Map<String, String> all = jedis.hgetAll(SERVICE_KEY);
            for (int i = 0; i < LOCAL_SERVICE.size(); i++) {
                List<String> list = CollectionUtil.toList(all.get(LOCAL_SERVICE.get(i)));
                serviceMap.put(LOCAL_SERVICE.get(i), list);
            }
            return serviceMap;
        }
        List<String> hosts = jedis.hmget(SERVICE_KEY, names); // todo 应该是按顺序返回，未确定
        for (int i = 0; i < names.length; i++) {
            serviceMap.put(names[i], CollectionUtil.toList(hosts.get(i)));
        }
//        for (int i = 0; i < LOCAL_SERVICE.size(); i++) {
//            List<String> list = CollectionUtil.toList(hosts.get(i));
//            serviceMap.put(LOCAL_SERVICE.get(i), list);
//        }
        return serviceMap;
    }

    public ConcurrentHashMap<String, List<String>> pull() {
        return pull(LOCAL_SERVICE.toArray(new String[0]));
    }

    private void push(Map<String, List<String>> serviceInfo) {
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
                if (entry.getValue() == null || entry.getValue().size() == 0) {
                    delField.add(entry.getKey());
                } else {
                    updateField.put(entry.getKey(), entry.getValue().toString());
                }
            }
            if (delField.size() > 0) {
                multi.hdel(SERVICE_KEY, delField.toArray(new String[0]));
            }
            if (updateField.size() > 0) {
                multi.hmset(SERVICE_KEY, updateField);
            }
            multi.exec();
        } catch (Exception e) {
            e.printStackTrace();
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
        CompletableFuture.supplyAsync(() -> {
            long time = Long.parseLong(KEEP_TIME);
            while (ALIVE) {
                try {
                    System.out.println("host heart beat:" + address);
                    jedisPool.getResource().setex(address, time, LOCAL_SERVICE.toString());
                    Thread.sleep(10000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
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
        String owner = jedis.get(LOCK_KEY);
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
                        jedis.set(LOCK_KEY, address, pn);
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
            jedisPool.getResource().eval("local sip = redis.call(\"get\",KEYS[1]);local lip = ARGV[1];if lip == sip then redis.call(\"del\",KEYS[1]);end;return \"OK\"", 1, LOCK_KEY, address);
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
}
