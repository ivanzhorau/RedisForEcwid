package org.example;

import by.zhorau.rediscollections.RedisList;
import by.zhorau.rediscollections.RedisMap;
import redis.clients.jedis.Jedis;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.Arrays;
import java.util.Map;
import java.util.Properties;

public class Main {
    public static void main(String[] args) throws IOException {
        Properties properties = new Properties();
        properties.load(new FileInputStream("src/main/resources/config.properties"));
        Jedis jedis = new Jedis(properties.getProperty("redis.url"));
        Map<String, Integer> map = new RedisMap(jedis, "test");
        map.put("a", 10);
        map.put("c", 3);
        System.out.println(map.keySet());
        System.out.println(map.values());
        System.out.println(map.entrySet());
        System.out.println(map.get("a")+" "+map.get("c"));
        map.remove("a");
        System.out.println(map.get("a")+" "+map.get("c"));
        map.clear();
        System.out.println(map.keySet());
        System.out.println(map.values());
        System.out.println(map.entrySet());


        RedisList list = new RedisList(jedis, "test");
        list.add("a");
        list.add("b");
        list.add("c");
        list.retainAll(Arrays.asList("a","c"));
        System.out.println(list.size());
        System.out.println(list);
        list.clear();

    }
}