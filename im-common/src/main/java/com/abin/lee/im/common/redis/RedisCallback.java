package com.abin.lee.im.common.redis;

import redis.clients.jedis.Jedis;

/**
 * Created by abin
 * Be Created in 2016/5/18.
 */
public interface RedisCallback<T> {

    T doInRequest(Jedis jedis);

}