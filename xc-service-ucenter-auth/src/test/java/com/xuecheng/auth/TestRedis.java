package com.xuecheng.auth;

import com.alibaba.fastjson.JSON;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.BoundValueOperations;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

@SpringBootTest
@RunWith(SpringRunner.class)
public class TestRedis {
    @Autowired
    private StringRedisTemplate stringRedisTemplate;

    @Test
    public  void  saveRedis(){
        //定义key
        String key = "596272756";
        //定义Map
        Map<String,String> mapValue = new HashMap<>();
        mapValue.put("id","101");
        mapValue.put("username","itcast");
        mapValue.put("jwt","eyJhbGciOiJSUzI1NiIsInR5cCI6IkpXVCJ9.eyJuYW1lIjoienpoIn0.KKOr4IV5Oh3l9gjoQJeGbDFUc6ngNkh2eDzS6z0Ap1pbkbjkNVBDmUUjCDNVZotGX6ScvkkDeAgphMRTINMtEbKKQJkj7tC2TvsyEwADmZ2UYoEnY5UnIveuRoK6owyS3pG94zkavG6OzaWSwMas0GH1ywGLtHu30cuKy0fXO2VxuD44CbPm6RkIIWR4hC4-ljTUtrkGXN1r5AxUuyNEmXUwUAc0YKh3rVXS3YyPY7SJfiuPBNaccFfV7ClTQJTv-qs14CokgnOxW_2nL6EPPZCcGNJk_-VoprtUZ4G7KMxLwfiEXVMVKoHWAtJvxf75sSrvj2YcW8ymjpIAQAQzLA");
        String value = JSON.toJSONString(mapValue);
        BoundValueOperations<String, String> stringStringBoundValueOperations = stringRedisTemplate.boundValueOps(key);
        stringStringBoundValueOperations.set(value,30, TimeUnit.SECONDS);
        Long expire = stringRedisTemplate.getExpire(key);
        System.out.println(expire);
    }
}
