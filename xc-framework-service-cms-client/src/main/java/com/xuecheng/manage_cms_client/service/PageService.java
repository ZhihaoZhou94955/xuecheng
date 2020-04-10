package com.xuecheng.manage_cms_client.service;

import com.mongodb.client.gridfs.GridFSBucket;
import com.mongodb.client.gridfs.GridFSDownloadStream;
import com.mongodb.client.gridfs.model.GridFSFile;
import com.xuecheng.framework.domain.cms.CmsPage;
import com.xuecheng.framework.domain.cms.CmsSite;
import com.xuecheng.framework.exception.ExceptionCast;
import com.xuecheng.framework.model.response.CommonCode;
import com.xuecheng.manage_cms_client.dao.cmsPageRepositry;
import com.xuecheng.manage_cms_client.dao.cmsSiteRepositry;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.IOUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.gridfs.GridFsResource;
import org.springframework.data.mongodb.gridfs.GridFsTemplate;
import org.springframework.stereotype.Service;

import java.io.*;
import java.util.Optional;

@Service
@Slf4j
public class PageService {

    @Autowired
    private cmsPageRepositry cmsPageRepositry;
    @Autowired
    private cmsSiteRepositry cmsSiteRepositry;
    @Autowired
    private GridFSBucket gridFSBucket;
    @Autowired
    private GridFsTemplate gridFsTemplate;

    public  void savePageToServerPath(String pageId){
        //通过 pageId 查询出 cmsPage对象
        Optional<CmsPage> optional = cmsPageRepositry.findById(pageId);
        if (optional.isPresent()){
            CmsPage cmsPage = optional.get();
            //获取htmlFileId
            String htmlFileId = cmsPage.getHtmlFileId();
            //通过htmlFileId 获取 生成的html页面内容
            String content = getFileById(htmlFileId);
            //获取siteId
            String siteId = cmsPage.getSiteId();
            Optional<CmsSite> optionalCmsSite = cmsSiteRepositry.findById(siteId);
            if (optional.isPresent()){
                //获取网站物理路径
                CmsSite cmsSite = optionalCmsSite.get();
                //获取页面路径
                String pageWebPath = cmsPage.getPageWebPath();
                String path=cmsSite.getSitePhysicalPath()+pageWebPath;
                //将html内容写入到path位置
                try {
                    FileOutputStream fileOutputStream=new FileOutputStream(new File(path));
                    IOUtils.write(content,fileOutputStream,"utf-8");
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

    }

    private String getFileById(String htmlFileId) {
        GridFSFile gridFSFile = gridFsTemplate.findOne(Query.query(Criteria.where("_id").is(htmlFileId)));
        //打开下载流
        GridFSDownloadStream gridFSDownloadStream = gridFSBucket.openDownloadStream(gridFSFile.getObjectId());
        //操作流
        GridFsResource gridFsResource = new GridFsResource(gridFSFile, gridFSDownloadStream);
        //获取输入流
        InputStream inputStream = null;
        String content="";
        try {
            inputStream = gridFsResource.getInputStream();
            if (inputStream==null){
                log.error("获取html内容失败！");
                ExceptionCast.cast(CommonCode.FAIL);
            }
            //从输入流中获取内容
            content = IOUtils.toString(inputStream, "utf-8");
        } catch (IOException e) {
            log.error("拉取html页面发生错误，{}",e.getMessage());
            e.printStackTrace();
        }
        return content;
    }

}
