package com.xuecheng.manage_cms;


import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.http.client.OkHttp3ClientHttpRequestFactory;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.web.client.RestTemplate;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

@SpringBootApplication
@EntityScan("com.xuecheng.framework.domain.cms")//扫描实体类
@ComponentScan("com.xuecheng.api.cms") //扫描接口
@ComponentScan("com.xuecheng.manage_cms") //扫描包下的所有类
@ComponentScan("com.xuecheng.framework") //扫描exception包下的注解
@EnableSwagger2
@EnableDiscoveryClient
public class CmsServiceApplication {
    public static void main(String[] args) {
        SpringApplication.run(CmsServiceApplication.class);
    }

    @Bean
    public RestTemplate getRestTemplate(){
        return  new RestTemplate(new OkHttp3ClientHttpRequestFactory());
    }
}
