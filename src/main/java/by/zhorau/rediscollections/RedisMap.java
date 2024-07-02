package by.zhorau.rediscollections;

import redis.clients.jedis.commands.JedisCommands;

import java.util.*;

public class RedisMap implements Map<String, Integer> {

    private final JedisCommands jedis;
    private final String namespace;

    public RedisMap(JedisCommands jedis, String namespace) {
        this.jedis = jedis;
        this.namespace = namespace;
    }

    private String modifyKey(Object key) {
        return namespace + ":" + key.toString();
    }

    @Override
    public int size() {
        return jedis.keys(namespace + ":*").size();
    }

    @Override
    public boolean isEmpty() {
        return size() == 0;
    }

    @Override
    public boolean containsKey(Object key) {
        return jedis.exists(modifyKey(key));
    }

    @Override
    public boolean containsValue(Object value) {
        for (String key : jedis.keys(namespace + ":*")) {
            if (jedis.get(key).equals(value.toString())) {
                return true;
            }
        }
        return false;
    }

    @Override
    public Integer get(Object key) {
        String value = jedis.get(modifyKey(key));
        if (value != null)
            return Integer.parseInt(value);
        else
            return null;
    }

    @Override
    public Integer put(String key, Integer value) {
        jedis.set(modifyKey(key), String.valueOf(value));
        return Integer.parseInt(jedis.get(modifyKey(key)));
    }

    @Override
    public Integer remove(Object key) {
        String stringValue = jedis.get(modifyKey(key));
        Integer value = null;
        if (stringValue != null)
            value = Integer.parseInt(jedis.get(modifyKey(key)));
        jedis.del(modifyKey(key));
        return value;
    }

    @Override
    public void putAll(Map<? extends String, ? extends Integer> m) {
        for (Entry<? extends String, ? extends Integer> entry : m.entrySet()) {
            put(entry.getKey(), entry.getValue());
        }
    }

    @Override
    public void clear() {
        for (String key : jedis.keys(namespace + ":*")) {
            jedis.del(key);
        }
    }

    @Override
    public Set<String> keySet() {
        return new HashSet<>(jedis.keys(namespace + ":*").stream().map(mKey-> mKey.split(":")[1]).toList());
    }

    @Override
    public Collection<Integer> values() {
        return new ArrayList<>(jedis.keys(namespace + ":*").stream().map(key -> Integer.parseInt(jedis.get(key))).toList());
    }

    @Override
    public Set<Entry<String, Integer>> entrySet() {
        return new HashSet<>(jedis.keys(namespace + ":*").stream().map(key -> new AbstractMap.SimpleEntry<>(
                key.split(":")[1], Integer.parseInt(jedis.get(key)))).toList());
    }
}
