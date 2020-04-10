package com.xuecheng.auth;

import com.sun.jersey.core.util.Base64;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.cloud.client.ServiceInstance;
import org.springframework.cloud.client.loadbalancer.LoadBalancerClient;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.util.Base64Utils;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.DefaultResponseErrorHandler;
import org.springframework.web.client.RestTemplate;

import javax.ws.rs.core.MultivaluedMap;
import java.io.IOException;
import java.net.URI;
import java.util.Map;

@SpringBootTest
@RunWith(SpringRunner.class)
public class ClaimToken {

    @Autowired
    private  LoadBalancerClient loadBalancerClient;

    @Autowired
    private RestTemplate restTemplate;
    @Test
    public  void  test(){
        //1. 拼接 申请令牌地址
        ServiceInstance serviceInstance = loadBalancerClient.choose("xc-service-ucenter-auth");
        URI uri = serviceInstance.getUri();
        String url=uri+"/auth/oauth/token";
        //headers
        MultiValueMap<String,String> headers=new LinkedMultiValueMap<>();
        //使用 httpbasic 协议  需要在 headers中添加 Authorization  它的值是 客户端名称:客户端密码 通过 base64加密而成
        String authorization="XcWebApp:XcWebApp";
        byte[] encode = Base64Utils.encode(authorization.getBytes());
        String s = "Basic " + new String(encode);
        headers.add("Authorization",s);
        //body
        MultiValueMap<String,String> body=new LinkedMultiValueMap<>();
        body.add("grant_type","password");
        body.add("username","itcast");
        body.add("password","123");
        HttpEntity<MultiValueMap<String, String>> httpEntity = new HttpEntity<>(body,headers);
        ResponseEntity<Map> entity = restTemplate.exchange(url, HttpMethod.POST, httpEntity, Map.class);
        restTemplate.setErrorHandler(new DefaultResponseErrorHandler(){
            @Override
            public void handleError(ClientHttpResponse response) throws IOException {
                if (response.getRawStatusCode()!=401||response.getRawStatusCode()!=400){
                  super.handleError(response);
                }
            }
        });
        Map map = entity.getBody();
        System.out.println(map);
    }

    private String httpbasic(String clientId,String clientSecret){
//将客户端id和客户端密码拼接，按“客户端id:客户端密码”
        String string = clientId+":"+clientSecret;
//进行base64编码
        byte[] encode = Base64.encode(string.getBytes());
        return "Basic "+new String(encode);
    }

}
