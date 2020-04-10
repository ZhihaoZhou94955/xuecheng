package com.xuecheng.manage_cms.controller;

import com.mongodb.MongoClient;
import com.xuecheng.api.cms.CmsPageControllerApi;
import com.xuecheng.framework.domain.cms.CmsConfig;
import com.xuecheng.framework.domain.cms.CmsPage;
import com.xuecheng.framework.domain.cms.request.QueryPageRequest;
import com.xuecheng.framework.domain.cms.response.CmsPageResult;
import com.xuecheng.framework.domain.cms.response.CmsPostPageResult;
import com.xuecheng.framework.model.response.CommonCode;
import com.xuecheng.framework.model.response.QueryResponseResult;
import com.xuecheng.framework.model.response.QueryResult;
import com.xuecheng.framework.model.response.ResponseResult;
import com.xuecheng.manage_cms.service.CmsPageService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;

@Api(value="cms页面管理接口",description = "cms页面管理接口，提供页面的增、删、改、查")
@RestController
@RequestMapping("/cms/page")
public class CmsPageController implements CmsPageControllerApi {


    @Autowired
    private CmsPageService cmsPageService;
    @Override
    @GetMapping("/list/{page}/{size}")
    @ApiOperation(value = "分页查询页面列表")
    @ApiImplicitParams({@ApiImplicitParam(name = "page", value = "页码", required = true, dataType = "int", paramType = "path"),
            @ApiImplicitParam(name = "size", value = "每页记录数", required = true, dataType = "int", paramType = "path")
    })
    public QueryResponseResult findList(@PathVariable("page") int page, @PathVariable("size") int size, QueryPageRequest pageRequest) {
        //1.查询cms_page
//        Query query=new Query();
//
//        MongoTemplate mongoTemplate=new MongoTemplate();

        return cmsPageService.cmsPage(pageRequest, page, size);
    }

//    添加CmsPage记录

    @Override
    @PostMapping("/add")
    public CmsPageResult addCmsPage(@RequestBody CmsPage cmsPage) {

        return cmsPageService.addCmsPage(cmsPage);

    }

    @Override
    @PutMapping("/edit")
    public CmsPageResult updateCmsPage(@RequestParam("pageId")String pageId,@RequestBody CmsPage cmsPage) {
        return cmsPageService.updateCmsPage(pageId,cmsPage);
    }

    @Override
    @GetMapping("/get/{pageId}")
    public CmsPage findCmsPageById(@PathVariable("pageId") String pageId) {
        return cmsPageService.findCmsPageById(pageId);
    }

    @Override
    @DeleteMapping("/del/{pageId}")
    public ResponseResult deleteCmsPage(@PathVariable("pageId") String pageId) {

        return cmsPageService.deleteCmsPage(pageId);

    }

    @Override
    @PostMapping("/postPage/{pageId}")
    public ResponseResult postpageCmspage(@PathVariable String pageId) {
        return cmsPageService.postpageCmspage(pageId);
    }

    @Override
    @PostMapping("/save")
    public CmsPageResult save(@RequestBody CmsPage cmsPage) {
        return cmsPageService.save(cmsPage);
    }

    @Override
    @PostMapping("/postPageQuick")
    public CmsPostPageResult postPageQuick(@RequestBody CmsPage cmsPage) {
        return cmsPageService.postPageQuick(cmsPage);
    }


}
