package com.coldwindx.loki.models;

import com.opencsv.CSVWriter;
import lombok.SneakyThrows;

import java.io.FileWriter;
import java.util.Collection;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

public class ConcurrentMapProxy<K, V> implements ConcurrentMap<K,V> {
    private final String name;
    private final ConcurrentHashMap<K, V> map;
    private final CSVWriter writer;

    @SneakyThrows
    ConcurrentMapProxy(String name, String path){
        this.name = name;
        this.map = new ConcurrentHashMap<>(16);
        this.writer = new CSVWriter(new FileWriter(path));
        writer.writeNext(new String[] {"name", "operation", "key", "value"});
    }

    @Override
    public int size() {
        return map.size();
    }

    @Override
    public boolean isEmpty() {
        return map.isEmpty();
    }

    @Override
    public boolean containsKey(Object key) {
        return map.containsKey(key);
    }

    @Override
    public boolean containsValue(Object value) {
        return map.containsValue(value);
    }

    @Override
    public V get(Object key) {
        return map.get(key);
    }

    @SneakyThrows
    @Override
    public V put(K key, V value) {
        writer.writeNext(new String[]{name, "put", key.toString(), value.toString()});
        writer.flush();
        return map.put(key, value);
    }

    @SneakyThrows
    @Override
    public V remove(Object key) {
        V value = map.remove(key);
        writer.writeNext(new String[]{name, "remove", key.toString(), value.toString()});
        writer.flush();
        return value;
    }

    @Override
    public void putAll(Map<? extends K, ? extends V> m) {
        map.putAll(m);
    }

    @SneakyThrows
    @Override
    public void clear() {
        writer.close();
        map.clear();
    }

    @Override
    public Set<K> keySet() {
        return map.keySet();
    }

    @Override
    public Collection<V> values() {
        return map.values();
    }

    @Override
    public Set<Entry<K, V>> entrySet() {
        return map.entrySet();
    }

    @Override
    public V putIfAbsent(K key, V value) {
        return map.putIfAbsent(key, value);
    }

    @SneakyThrows
    @Override
    public boolean remove(Object key, Object value) {
        writer.writeNext(new String[]{name, "remove", key.toString(), value.toString()});
        writer.flush();
        return map.remove(key, value);
    }

    @Override
    public boolean replace(K key, V oldValue, V newValue) {
        return map.replace(key, oldValue, newValue);
    }

    @Override
    public V replace(K key, V value) {
        return map.replace(key, value);
    }
}
