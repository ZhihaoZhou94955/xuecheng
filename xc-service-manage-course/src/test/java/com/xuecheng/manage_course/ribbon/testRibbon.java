package com.xuecheng.manage_course.ribbon;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.web.client.RestTemplate;

import java.util.Map;

@SpringBootTest
@RunWith(SpringRunner.class)
public class testRibbon {
    @Autowired
    private RestTemplate restTemplate;
    @Test
    public void test(){
        String serviceId="XC-SERVICE-MANAGE-CMS";
        for (int i = 0; i < 10; i++) {
            ResponseEntity<Map> forEntity = restTemplate.getForEntity("http://" + serviceId + "/cms/page/list/1/10", Map.class);
            Map body = forEntity.getBody();
            System.out.println(body);
        }
    }

}
