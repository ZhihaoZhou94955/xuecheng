package com.xuecheng.manage_course;

import com.xuecheng.framework.domain.cms.CmsConfig;
import com.xuecheng.framework.domain.cms.request.QueryPageRequest;
import com.xuecheng.framework.model.response.QueryResponseResult;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient("xc-service-manage-cms")
public interface CourseCmsClient {

        @GetMapping("/cms/config/getmodel/{id}")
        public CmsConfig getmodel(@PathVariable String id);
}
