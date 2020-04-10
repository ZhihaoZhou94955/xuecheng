package com.xuecheng.search.controller;

import com.xuecheng.api.search.SearchControllerApi;
import com.xuecheng.framework.domain.course.CoursePub;
import com.xuecheng.framework.domain.course.TeachplanMediaPub;
import com.xuecheng.framework.domain.search.CourseSearchParam;
import com.xuecheng.framework.exception.ExceptionCast;
import com.xuecheng.framework.model.response.CommonCode;
import com.xuecheng.framework.model.response.QueryResponseResult;
import com.xuecheng.search.Service.CourseSearchSerivce;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.index.query.TermQueryBuilder;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.SearchHits;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/search/course")
public class SearchController implements SearchControllerApi {

    @Autowired
    private CourseSearchSerivce courseSearchSerivce;

    /**
     *
     * @param page
     * @param size
     * @param courseSearchParam  用来接收传过来的查询条件
     * @return
     * @throws IOException
     */
    @Override
    @GetMapping("/list/{page}/{size}")
    public QueryResponseResult<CoursePub> list(@PathVariable("page") int page,
                                               @PathVariable("size") int size, CourseSearchParam courseSearchParam)  {
        return courseSearchSerivce.list(page,size,courseSearchParam);
    }

    @Override
    @GetMapping("/getall/{id}")
    public Map<String, CoursePub> getall(@PathVariable String id) {
       return  courseSearchSerivce.getall(id);
    }

    @Override
    @GetMapping(value="/getmedia/{teachplanId}")
    public TeachplanMediaPub getmedia(@PathVariable String teachplanId) {
        String[] strings=new String[]{teachplanId};
        QueryResponseResult<TeachplanMediaPub> responseResult = courseSearchSerivce.getmedia(strings);
        List<TeachplanMediaPub> list = responseResult.getQueryResult().getList();
        if (list==null||list.size()==0){
            return new TeachplanMediaPub();
        }
        return list.get(0);
    }

}
