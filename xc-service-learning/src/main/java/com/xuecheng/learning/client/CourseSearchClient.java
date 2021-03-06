package com.xuecheng.learning.client;

import com.xuecheng.framework.domain.course.TeachplanMediaPub;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient("xc-search-service")
public interface CourseSearchClient {

    @GetMapping(value="/search/course/getmedia/{teachplanId}")
    public TeachplanMediaPub getmedia(@PathVariable String teachplanId);
}
