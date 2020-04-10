package com.xuecheng.manage_cms_client.mq;

import com.alibaba.fastjson.JSON;
import com.xuecheng.framework.domain.cms.CmsPage;
import com.xuecheng.manage_cms_client.dao.cmsPageRepositry;
import com.xuecheng.manage_cms_client.service.PageService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.Optional;

@Component
@Slf4j
public class rabbitmqListener {

    @Autowired
    private PageService pageService;
    @Autowired
    private cmsPageRepositry cmsPageRepositry;


    @RabbitListener(queues={"${xuecheng.mq.queue}"})
    public void postPage(String message){
        //获取到的消息是json格式 转换成map集合
        Map map = JSON.parseObject(message, Map.class);
        String pageId = (String) map.get("pageId");
       //校验页面是否合法
        Optional<CmsPage> optional = cmsPageRepositry.findById(pageId);
        if (!optional.isPresent()){
            log.error("receive postpage msg, cmsPage is null,pageId:{}",pageId);
            return;
        }
        pageService.savePageToServerPath(pageId);
    }
}
