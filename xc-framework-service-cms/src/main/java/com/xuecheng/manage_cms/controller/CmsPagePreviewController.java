package com.xuecheng.manage_cms.controller;

import com.xuecheng.framework.web.BaseController;
import com.xuecheng.manage_cms.service.PageService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import javax.servlet.ServletOutputStream;
import java.io.IOException;

@Controller
public class CmsPagePreviewController extends BaseController {

        @Autowired
   PageService pageService;

   @RequestMapping(value="/cms/preview/{pageId}",method = RequestMethod.GET)
   public void preview(@PathVariable("pageId")String pageId) throws IOException {
        String html = pageService.createHTML(pageId);
        ServletOutputStream outputStream = response.getOutputStream();
        response.setHeader("Content-type","text/html;charset=utf-8");
        //输出静态页面内容
        outputStream.write(html.getBytes());

        }

}
