package com.xuecheng.govern.gateway.service;

import com.xuecheng.framework.utils.CookieUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import java.util.Map;
import java.util.concurrent.TimeUnit;

@Service
public class AuthService {
    @Autowired
    StringRedisTemplate stringRedisTemplate;

    public String getTokenFromCookie(HttpServletRequest request){
        Map<String, String> map = CookieUtil.readCookie(request, "uid");
        if (map== null || map.get("uid")==null){
            return null;
        }
        String uid = map.get("uid");
        return uid;
    }

    public String getJwtFromHeader(HttpServletRequest request){
        String authorization = request.getHeader("Authorization");
        if (authorization==null)
            return null;
        String jwt = authorization.substring(7);
        return jwt;
    }

    //查询令牌的有效期
    public long getExpire(String access_token){
        Long expire = stringRedisTemplate.getExpire("user_token:" + access_token, TimeUnit.SECONDS);
        return  expire;
    }
}
