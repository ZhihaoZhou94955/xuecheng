package com.xuecheng.manage_course.feign;

import com.xuecheng.framework.domain.cms.CmsConfig;
import com.xuecheng.framework.model.response.QueryResponseResult;
import com.xuecheng.manage_course.CourseCmsClient;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.List;

@SpringBootTest
@RunWith(SpringRunner.class)
public class testFeign {

    @Autowired
    private CourseCmsClient courseCmsClient;

    @Test
    public  void test(){
        CmsConfig getmodel = courseCmsClient.getmodel("5a791725dd573c3574ee333f");
        System.out.println(getmodel);
    }
}
