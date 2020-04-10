package com.xuecheng.api.search;

import com.xuecheng.framework.domain.course.CoursePub;
import com.xuecheng.framework.domain.course.TeachplanMediaPub;
import com.xuecheng.framework.domain.search.CourseSearchParam;
import com.xuecheng.framework.model.response.QueryResponseResult;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;

import java.io.IOException;
import java.util.Map;

@Api(value = "课程搜索",description = "课程搜索",tags = {"课程搜索"})
public interface SearchControllerApi {

    @ApiOperation("课程搜索")
    public QueryResponseResult<CoursePub> list(int page, int size, CourseSearchParam courseSearchParam) throws IOException;

    public Map<String,CoursePub> getall(String id);
    @ApiOperation("根据课程计划查询媒")
    public TeachplanMediaPub  getmedia(String teachplanId);
}
