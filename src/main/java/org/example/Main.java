package org.example;

import by.zhorau.rediscollections.RedisList;
import by.zhorau.rediscollections.RedisMap;
import redis.clients.jedis.Jedis;
import java.util.Arrays;
import java.util.Map;

public class Main {
    public static void main(String[] args) {
        Jedis jedis = new Jedis("redis://default:d4hmrGB1UKPIfQ6kGcgzR9fIEU4VZCGT@redis-10724.c327.europe-west1-2.gce.redns.redis-cloud.com:10724");
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