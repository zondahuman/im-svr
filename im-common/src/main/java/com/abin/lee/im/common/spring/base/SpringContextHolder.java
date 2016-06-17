package com.abin.lee.im.common.spring.base;

import com.abin.lee.im.common.redis.RedisService;
import com.abin.lee.im.common.spring.context.SpringContextUtils;

/**
 * Created with IntelliJ IDEA.
 * User: tinkpad
 * Date: 16-6-15
 * Time: 下午10:30
 * To change this template use File | Settings | File Templates.
 */
public class SpringContextHolder {

    public static RedisService getRedisService(){
        RedisService redisService = SpringContextUtils.getBean("redisService", RedisService.class);
        return redisService;
    }


}
