package com.miex.cache;

public interface Cache<K,V> {
    void put(K k, V v);

    V get(K k);
}
