package com.wncud.redis;

import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;

/**
 * Created by yajunz on 2014/10/20.
 */
public class MyRedisClient {
    public static void main(String[] args) {
        JedisPool pool = new JedisPool(new JedisPoolConfig(), "192.168.69.10", 6379);
        Jedis jedis = pool.getResource();

        jedis.set("test", "hello world!!");

        jedis.lpush("list", "value1");
        jedis.lpush("list", "value2");
        jedis.lpush("list", "value3");
        jedis.lpush("list", "value4");

        while(jedis.llen("list") > 0){
            System.out.println( jedis.rpop("list"));
        }

        String value = jedis.get("test");
        System.out.println(value);

        pool.returnResource(jedis);
        pool.destroy();
    }
}
