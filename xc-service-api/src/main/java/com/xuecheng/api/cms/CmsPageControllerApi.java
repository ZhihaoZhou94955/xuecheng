package com.xuecheng.api.cms;


import com.xuecheng.framework.domain.cms.CmsConfig;
import com.xuecheng.framework.domain.cms.CmsPage;
import com.xuecheng.framework.domain.cms.request.QueryPageRequest;
import com.xuecheng.framework.domain.cms.response.CmsPageResult;
import com.xuecheng.framework.domain.cms.response.CmsPostPageResult;
import com.xuecheng.framework.model.response.QueryResponseResult;
import com.xuecheng.framework.model.response.ResponseResult;

public interface CmsPageControllerApi  {

    /**
     * 页面分页查询
     * @param page
     * @param size
     * @param pageRequest
     * @return
     */
    public QueryResponseResult findList(int page, int size, QueryPageRequest pageRequest);

    /**
     * 添加CmsPage记录
     * @param cmsPage
     * @return
     */
    public CmsPageResult addCmsPage(CmsPage cmsPage);

    /**
     * 修改CmsPage记录
     */
    public CmsPageResult updateCmsPage(String pageId,CmsPage cmsPage);

    /**
     * 通过pageId查询CmsPage
     */
    public CmsPage findCmsPageById(String pageId);

    /**
     * 通过PageId删除CmsPage
     */
    public ResponseResult deleteCmsPage(String pageId);


    /**
     * 通过PageId发布CmsPage
     */
    public ResponseResult postpageCmspage(String pageId);

    /**
     * 保存Cmspage
     */
    public CmsPageResult save(CmsPage cmsPage);

    /**
     * 一键发布页面
     */
    public CmsPostPageResult postPageQuick(CmsPage cmsPage);
}
