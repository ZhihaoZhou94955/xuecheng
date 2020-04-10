package com.xuecheng.manage_cms.dao;

import com.xuecheng.framework.domain.cms.CmsTemplate;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface cmsTemplateRepositry extends MongoRepository<CmsTemplate,String> {

    public  CmsTemplate findByTemplateFileId(String TemplateFileId);
}
