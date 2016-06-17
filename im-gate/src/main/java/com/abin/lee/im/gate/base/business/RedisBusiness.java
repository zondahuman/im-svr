package com.abin.lee.im.gate.base.business;

import com.abin.lee.im.common.redis.RedisService;
import com.abin.lee.im.common.spring.base.SpringContextHolder;
import com.abin.lee.im.common.spring.context.SpringContextUtils;
import com.abin.lee.im.gate.base.enums.UserConstantEnum;
import redis.clients.jedis.ShardedJedisPool;

/**
 * Created with IntelliJ IDEA.
 * User: tinkpad
 * Date: 16-6-15
 * Time: 下午10:26
 * To change this template use File | Settings | File Templates.
 */
public class RedisBusiness {

    public void setUser(String key, String value){
        RedisService redisService = SpringContextHolder.getRedisService();
        redisService.set(key, value, "NX", "EX", UserConstantEnum.REDIS_EXPIRE_TIME.getParam());
    }

    public String getUser(String key){
        RedisService redisService = SpringContextHolder.getRedisService();
        return redisService.get(key);
    }


}
