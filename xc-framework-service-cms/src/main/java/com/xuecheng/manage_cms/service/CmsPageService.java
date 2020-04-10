package com.xuecheng.manage_cms.service;

import com.alibaba.fastjson.JSON;
import com.xuecheng.framework.domain.cms.CmsConfig;
import com.xuecheng.framework.domain.cms.CmsPage;
import com.xuecheng.framework.domain.cms.CmsSite;
import com.xuecheng.framework.domain.cms.CmsTemplate;
import com.xuecheng.framework.domain.cms.request.QueryPageRequest;
import com.xuecheng.framework.domain.cms.response.CmsCode;
import com.xuecheng.framework.domain.cms.response.CmsPageResult;
import com.xuecheng.framework.domain.cms.response.CmsPostPageResult;
import com.xuecheng.framework.exception.ExceptionCast;
import com.xuecheng.framework.model.response.CommonCode;
import com.xuecheng.framework.model.response.QueryResponseResult;
import com.xuecheng.framework.model.response.QueryResult;
import com.xuecheng.framework.model.response.ResponseResult;
import com.xuecheng.manage_cms.config.rabbitmqConfig;
import com.xuecheng.manage_cms.dao.CmsSiteRepositry;
import com.xuecheng.manage_cms.dao.cmsConfigRepositry;
import com.xuecheng.manage_cms.dao.cmsPageRepositry;
import com.xuecheng.manage_cms.dao.cmsTemplateRepositry;
import freemarker.template.Configuration;
import freemarker.template.Template;
import freemarker.template.TemplateException;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.bson.types.ObjectId;
import org.springframework.amqp.core.AmqpTemplate;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.*;
import org.springframework.data.mongodb.gridfs.GridFsTemplate;
import org.springframework.http.ResponseEntity;
import org.springframework.http.codec.CodecConfigurer;
import org.springframework.stereotype.Service;
import org.springframework.ui.freemarker.FreeMarkerTemplateUtils;
import org.springframework.web.client.RestTemplate;

import java.io.*;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;



@Service
public class CmsPageService {

    @Autowired
    private CmsSiteRepositry cmsSiteRepositry;
    @Autowired
    private GridFsTemplate gridFsTemplate;
    @Autowired
    private cmsPageRepositry cmsPageRepositry;
    @Autowired
    private cmsConfigRepositry cmsConfigRepositry;
    @Autowired
    private PageService pageService;
    @Autowired
    private RabbitTemplate rabbitTemplate;
    @Autowired
    private RestTemplate restTemplate;
    //分页查询
    public QueryResponseResult cmsPage(QueryPageRequest pageRequest,int page,int size){
        //当页面是0或者小于0将 0赋值给page  因为MongoDB是0算第一页
        if (page<=0) page=0;
        else page=page-1;
        //创建分页对象
        Pageable pageable= PageRequest.of(page,size);
        //创建查询对象
        CmsPage cmsPage = new CmsPage();
        if(StringUtils.isNotEmpty(pageRequest.getPageAliase()))
            cmsPage.setPageAliase(pageRequest.getPageAliase());
        if(StringUtils.isNotEmpty(pageRequest.getSiteId()))
            cmsPage.setSiteId(pageRequest.getSiteId());

        //创建条件匹配器
        ExampleMatcher matcher = ExampleMatcher.matching().withMatcher("pageAliase",
                ExampleMatcher.GenericPropertyMatchers.contains());
        Example<CmsPage> example = Example.of(cmsPage, matcher);

        Page<CmsPage> cmsPages = cmsPageRepositry.findAll(example, pageable);

        QueryResult queryResult = new QueryResult();
        queryResult.setList(cmsPages.getContent());
        queryResult.setTotal(cmsPages.getTotalElements());

        return new QueryResponseResult(CommonCode.SUCCESS,queryResult);
    }

    //新增CmsPage
    public CmsPageResult addCmsPage(CmsPage cmsPage) {
        if (cmsPage==null){
            ExceptionCast.cast(CmsCode.CMS_PARAMS_NULL);
        }
        //判断cmspage是否已存在
        CmsPage cmsPage1 = cmsPageRepositry.findByPageNameAndSiteIdAndPageWebPath(
                cmsPage.getPageName(), cmsPage.getSiteId(), cmsPage.getPageWebPath());
        //如果存在
        if (cmsPage1!=null){
           //抛出异常
            //异常处理没有生效 是因为异常处理的注解没有被引导类扫描到 所以要在引导类上添加注解 扫描异常处理类的注解
            ExceptionCast.cast(CmsCode.CMS_ADDPAGE_EXISTSNAME);
        }
        //插入数据，返回结果
        cmsPageRepositry.insert(cmsPage);
        return new CmsPageResult(CommonCode.SUCCESS,null);

    }

    //修改CmsPage 只更新传过来不为空的属性，空的不更新
    public CmsPageResult updateCmsPage(String pageId,CmsPage cmsPage) {
        //通过id查出来的CmsPage
        CmsPage one = findCmsPageById(pageId);
        if(one==null){
            ExceptionCast.cast(CmsCode.CMS_PARAMS_NULL);
        }
        //更新模板id
        one.setTemplateId(cmsPage.getTemplateId());
        //更新所属站点
        one.setSiteId(cmsPage.getSiteId());
        //更新页面别名
        one.setPageAliase(cmsPage.getPageAliase());
        //更新页面名称
        one.setPageName(cmsPage.getPageName());
        //更新访问路径
        one.setPageWebPath(cmsPage.getPageWebPath());
        //更新DataUrl
        one.setDataUrl(cmsPage.getDataUrl());
        //更新物理路径
        one.setPagePhysicalPath(cmsPage.getPagePhysicalPath());
        //保存
        CmsPage save = cmsPageRepositry.save(one);
        //如果返回的对象不为空
        if (save==null){
            ExceptionCast.cast(CmsCode.CMS_SAVE_FAIL);
        }
        //返回成功 并将cmspage对象返回
        return  new CmsPageResult(CommonCode.SUCCESS,save);
    }

    //通过Id查询CmsPage 返回CmsPage
    public CmsPage findCmsPageById(String pageId) {

        Optional<CmsPage> optional = cmsPageRepositry.findById(pageId);
        //判断optional是否为空
        if (!optional.isPresent()){
            ExceptionCast.cast(CmsCode.CMS_PAGE_NOTEXISTS);
        }

        return optional.get();
    }

    //通过PageId删除CmsPage
    public ResponseResult deleteCmsPage(String pageId) {
        //先判断CmsPage对象是否存在
        CmsPage cmsPageById = findCmsPageById(pageId);

        if (cmsPageById==null){
            ExceptionCast.cast(CmsCode.CMS_PARAMS_NULL);
        }
        cmsPageRepositry.deleteById(pageId);
        return new ResponseResult(CommonCode.SUCCESS);
    }

    //获取模型数据
    public CmsConfig getmodel(String id) {
        Optional<CmsConfig> config = cmsConfigRepositry.findById(id);
        if (!config.isPresent()){
           return  null;
        }
        CmsConfig cmsConfig = config.get();
        return  cmsConfig;
    }


    public ResponseResult postpageCmspage(String pageId) {
        //执行静态化程序
        String content = pageService.createHTML(pageId);
        //将生成的html页面保存到GridFS中
        saveHtml(pageId, content);
        //发送消息
        sendPostPage(pageId);
        return new ResponseResult(CommonCode.SUCCESS);
    }

    private void saveHtml(String pageId, String content) {
        try {
            InputStream inputStream = IOUtils.toInputStream(content, "utf-8");
            CmsPage cmsPage = this.findCmsPageById(pageId);
            String pageName = cmsPage.getPageName();
            ObjectId id = gridFsTemplate.store(inputStream, pageName);
            cmsPage.setHtmlFileId(id.toString());
            cmsPageRepositry.save(cmsPage);

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void sendPostPage(String pageId){
        CmsPage cmsPage = this.findCmsPageById(pageId);
        String siteId = cmsPage.getSiteId();
        Map map=new HashMap();
        map.put("pageId",pageId);
        String s = JSON.toJSONString(map);
        //将消息发送给指定的交换机
        rabbitTemplate.convertAndSend(rabbitmqConfig.EX_ROUTING_CMS_POSTPAGE,siteId,s);
    }

    public CmsPageResult save(CmsPage cmsPage) {
        //先判断页面是否存在
        CmsPage page = cmsPageRepositry.findByPageNameAndSiteIdAndPageWebPath(cmsPage.getPageName(), cmsPage.getSiteId(), cmsPage.getPageWebPath());
        if (page!=null){
            //如果存在则通过页面Id更新页面内容
            return updateCmsPage(page.getPageId(),cmsPage);
        }
        //否则 新建一个页面
        return addCmsPage(cmsPage);
    }

    public CmsPostPageResult postPageQuick(CmsPage cmsPage) {
        //1. 添加或修改页面
        CmsPageResult cmsPageResult = save(cmsPage);
        if (!cmsPageResult.isSuccess())
        {
            return new CmsPostPageResult(CommonCode.FAIL,null);
        }
        CmsPage page = cmsPageResult.getCmsPage();
        //2. 发布页面
        String pageId = page.getPageId();
        ResponseResult responseResult = this.postpageCmspage(pageId);
        if (!responseResult.isSuccess()){
            return new CmsPostPageResult(CommonCode.FAIL,null);
        }
        //3. 拼接url
        String siteId = page.getSiteId();
        CmsSite cmsSite = getCmsSite(siteId);
        String siteDomain = cmsSite.getSiteDomain();
        String siteWebPath = cmsSite.getSiteWebPath();
        String pageWebPath = page.getPageWebPath();
        String pageName = page.getPageName();
        String pageUrl=siteDomain+siteWebPath+pageWebPath+pageName;

        return new  CmsPostPageResult(CommonCode.SUCCESS,pageUrl);
    }

    public CmsSite getCmsSite(String siteId) {
        Optional<CmsSite> optional = cmsSiteRepositry.findById(siteId);
        if (!optional.isPresent()){
            ExceptionCast.cast(CommonCode.QUERY_FAIL);
        }
        return optional.get();
    }
}
