package com.xuecheng.auth.service;

import com.alibaba.fastjson.JSON;
import com.xuecheng.framework.domain.ucenter.ext.AuthToken;
import com.xuecheng.framework.domain.ucenter.request.LoginRequest;
import com.xuecheng.framework.domain.ucenter.response.AuthCode;
import com.xuecheng.framework.domain.ucenter.response.LoginResult;
import com.xuecheng.framework.exception.ExceptionCast;
import com.xuecheng.framework.model.response.CommonCode;
import com.xuecheng.framework.utils.CookieUtil;
import org.apache.commons.lang3.StringUtils;
import org.apache.tomcat.jni.Mmap;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.client.ServiceInstance;
import org.springframework.cloud.client.loadbalancer.LoadBalancerClient;
import org.springframework.data.redis.core.BoundValueOperations;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.stereotype.Service;
import org.springframework.util.Base64Utils;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.DefaultResponseErrorHandler;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.context.request.RequestContextHolder;

import java.io.IOException;
import java.net.URI;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

@Service
public class authService {

    @Value("${auth.tokenValiditySeconds}")
    private  Long tokenValiditySeconds;
    @Autowired
    private RestTemplate restTemplate;
    @Autowired
    private LoadBalancerClient loadBalancerClient;
    @Autowired
    private StringRedisTemplate stringRedisTemplate;
    /**
     * 申请令牌
     * @param loginRequest 用户登录的信息
     * @return
     */
    public AuthToken login(LoginRequest loginRequest,String clientid,String clientSecret) {
        //申请令牌
        AuthToken authToken = claimToken(loginRequest,clientid,clientSecret);
        if (authToken==null){
            ExceptionCast.cast(AuthCode.AUTH_LOGIN_ERROR);
        }

        //将令牌内容 存入 redis  设置key
        boolean b = saveToken(authToken);
        if (b==false){
            ExceptionCast.cast(AuthCode.AUTH_LOGIN_TOKEN_SAVEFAIL);
        }
        return authToken;
    }

    private boolean saveToken(AuthToken authToken) {
        String key="user_token:"+authToken.getAccess_token();
        BoundValueOperations<String, String> operations = stringRedisTemplate.boundValueOps(key);
        String content = JSON.toJSONString(authToken);
        operations.set(content,tokenValiditySeconds, TimeUnit.SECONDS);
        Long expire = stringRedisTemplate.getExpire(key);
        if (expire!=-2){
            return true;
        }
        return false;
    }

    private AuthToken claimToken(LoginRequest loginRequest,String clientid,String clientSecret) {
        ServiceInstance serviceInstance = loadBalancerClient.choose("xc-service-ucenter-auth");
        URI uri = serviceInstance.getUri();
        String url=uri+"/auth/oauth/token";
        //headers
        MultiValueMap<String,String> headers=new LinkedMultiValueMap<>();
        //使用 httpbasic 协议  需要在 headers中添加 Authorization  它的值是 客户端名称:客户端密码 通过 base64加密而成
        String authorization=clientid+":"+clientSecret;
        byte[] encode = Base64Utils.encode(authorization.getBytes());
        String s = "Basic " + new String(encode);
        headers.add("Authorization",s); //
        //body
        MultiValueMap<String,String> body=new LinkedMultiValueMap<>();
        body.add("grant_type","password");
        body.add("username",loginRequest.getUsername());
        body.add("password",loginRequest.getPassword());
        HttpEntity<MultiValueMap<String, String>> httpEntity = new HttpEntity<>(body,headers);
        restTemplate.setErrorHandler(new DefaultResponseErrorHandler(){
            @Override
            public void handleError(ClientHttpResponse response) throws IOException {
                if (response.getRawStatusCode()!=401 && response.getRawStatusCode()!=400){
                    super.handleError(response);
                }
            }
        });
        ResponseEntity<Map> entity = restTemplate.exchange(url, HttpMethod.POST, httpEntity, Map.class);
        Map map = entity.getBody();
        String error_description = (String) map.get("error_description");
        if(StringUtils.isNotEmpty(error_description)&&map!=null){
            if(error_description.equals("坏的凭证")){
                ExceptionCast.cast(AuthCode.AUTH_CREDENTIAL_ERROR);
            }else if(error_description.indexOf("UserDetailsService returned null")>=0){
                ExceptionCast.cast(AuthCode.AUTH_ACCOUNT_NOTEXISTS);
            }
        }
        if(map.get("access_token") == null || map.get("refresh_token") == null || map.get("jti") == null){//jti是jwt令牌的唯一标识作为用户身份令牌
           return null;
        }
        AuthToken authToken = new AuthToken();
        String access_token = (String) map.get("jti");
        String refresh_token = (String)map.get("refresh_token");
        String jwt_token = (String) map.get("access_token");
        authToken.setAccess_token(access_token);
        authToken.setJwt_token(jwt_token);
        authToken.setRefresh_token(refresh_token);
        return authToken;
    }

    public AuthToken findjwt(String key) {
        String s = stringRedisTemplate.opsForValue().get(key);
        //没有查出 返回空
        if (StringUtils.isEmpty(s)) {
            return null;
        }
        AuthToken authToken = JSON.parseObject(s, AuthToken.class);
        return authToken;
    }

    public Boolean deletejwt(String key) {
        stringRedisTemplate.delete(key);
        return true;
    }
}
