package com.xuecheng.manage_cms.dao;

import com.xuecheng.framework.domain.cms.CmsPage;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.*;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.List;
import java.util.Optional;

@SpringBootTest
@RunWith(SpringRunner.class)
public class testMongodb {

    @Autowired
    private cmsPageRepositry cmsPageRepostiry;

    //查询所有
    @Test
    public  void  testmongo(){
        List<CmsPage> pages = cmsPageRepostiry.findAll();
        for (CmsPage page : pages) {
            System.out.println(page);
        }
    }

    //分页查询
    @Test
    public  void  Paging(){

        Pageable pageable=PageRequest.of(3,5);
        Page<CmsPage> pages = cmsPageRepostiry.findAll(pageable);
        for (CmsPage page : pages) {
            System.out.println(page);
        }
    }



    //自定义分页条件查询
    @Test
    public  void  testPagequery(){
        //创建分页对象
        PageRequest pageRequest = PageRequest.of(0, 8);

        //创建查询条件
        CmsPage cmsPage=new CmsPage();
        cmsPage.setSiteId("5a751fab6abb5044e0d19ea1");
//        cmsPage.setTemplateId("5a962bf8b00ffc514038fafa");
        cmsPage.setPageAliase("详情");
        //创建条件匹配器
        ExampleMatcher exampleMatcher= ExampleMatcher.matching();
        ExampleMatcher matcher = exampleMatcher.withMatcher("pageAliase", ExampleMatcher.GenericPropertyMatchers.contains());
        Example<CmsPage> example = Example.of(cmsPage, matcher);
//        exampleMatcher.withMatcher()
        Page<CmsPage> cmsPages = cmsPageRepostiry.findAll(example, pageRequest);
        List<CmsPage> content = cmsPages.getContent();
        for (CmsPage page : content) {
            System.out.println(page);
        }
        System.out.println("total:"+cmsPages.getTotalElements());
    }
}