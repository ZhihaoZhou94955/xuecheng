package com.xuecheng.search.Service;

import com.xuecheng.framework.domain.course.CoursePub;
import com.xuecheng.framework.domain.course.TeachplanMediaPub;
import com.xuecheng.framework.domain.course.response.CourseCode;
import com.xuecheng.framework.domain.search.CourseSearchParam;
import com.xuecheng.framework.exception.ExceptionCast;
import com.xuecheng.framework.model.response.CommonCode;
import com.xuecheng.framework.model.response.QueryResponseResult;
import com.xuecheng.framework.model.response.QueryResult;
import com.xuecheng.framework.model.response.ResponseResult;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.common.text.Text;
import org.elasticsearch.index.query.*;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.SearchHits;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.elasticsearch.search.fetch.subphase.highlight.HighlightBuilder;
import org.elasticsearch.search.fetch.subphase.highlight.HighlightField;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@Slf4j
public class CourseSearchSerivce {

    @Autowired
    private RestHighLevelClient highLevelClient;

    @Value("${xuecheng.course.index}")
    private String index;
    @Value("${xuecheng.course.type}")
    private String type;
    @Value("${xuecheng.course.source_field}")
    private String source_field;
    @Value("${xuecheng.media.index}")
    private String media_index;
    @Value("${xuecheng.media.type}")
    private String media_type;
    @Value("${xuecheng.media.source_field}")
    private String media_source_field;

    /**
     * 因为课程查询出来是 分页显示的  所以这里我们要将查询出的文档进行分页
     * @return
     */
    public QueryResponseResult<CoursePub> list(int page, int size, CourseSearchParam courseSearchParam){
        //1. 创建一个 搜索请求对象  设置要查询的索引库名称和类型
        SearchRequest searchRequest=new SearchRequest(index);
        searchRequest.types(type);
        //2. 构建搜索源
        SearchSourceBuilder searchSourceBuilder=new SearchSourceBuilder();
        //设置要显示的关键词
        String[] split = source_field.split(",");
        searchSourceBuilder.fetchSource(split,new String[]{});
        //设置分页
        if (page<=0){
            page=1;
        }
        searchSourceBuilder.from((page-1)*size);
        if(size<=0){
            size=12;
        }
        searchSourceBuilder.size(size);
        //3. 使用布尔查询
        BoolQueryBuilder boolQueryBuilder=new BoolQueryBuilder();
        //如果有关键词
        if (StringUtils.isNotEmpty(courseSearchParam.getKeyword())){
            //4. 使用多字段查询 multimatchquery  关键词匹配百分比70  如果name字段中出现关键词 得分*10
            MultiMatchQueryBuilder multiMatchQueryBuilder = QueryBuilders.multiMatchQuery(courseSearchParam.getKeyword(),
                    new String[]{"name", "teachplan", "description"}).minimumShouldMatch("70").field("name",10);
            //将multimatchquery添加到boolQueryBuilder中
            boolQueryBuilder.must(multiMatchQueryBuilder);
        }
        if (StringUtils.isNotEmpty(courseSearchParam.getMt())){
            boolQueryBuilder.filter(QueryBuilders.termQuery("mt",courseSearchParam.getMt()));
        }
        if (StringUtils.isNotEmpty(courseSearchParam.getSt())){
            boolQueryBuilder.filter(QueryBuilders.termQuery("st",courseSearchParam.getSt()));
        }
        if (StringUtils.isNotEmpty(courseSearchParam.getGrade())){
            boolQueryBuilder.filter(QueryBuilders.termQuery("grade",courseSearchParam.getGrade()));
        }
        //设置关键词高亮
        HighlightBuilder highlightBuilder=new HighlightBuilder();
        highlightBuilder.preTags("<font class='eslight'>");
        highlightBuilder.postTags("</font>");
        highlightBuilder.fields().add(new HighlightBuilder.Field("name"));
        searchSourceBuilder.highlighter(highlightBuilder);
        //5. 将boolQueryBuilder添加到searchSourceBuilder
        searchSourceBuilder.query(boolQueryBuilder);
        //6. 将searchSourceBuilder传入searchRequest
        searchRequest.source(searchSourceBuilder);
        //7. 查询
        QueryResult<CoursePub> queryResult = new QueryResult<>();
        ArrayList<CoursePub> pubArrayList = new ArrayList<>();
        SearchResponse response =null;
        try {
            response = highLevelClient.search(searchRequest);
            //获取符合条件的文档
        } catch (IOException e) {
            log.error("【搜索服务】 ES搜索失败！");
            e.printStackTrace();
        }
        SearchHit[] hits = response.getHits().getHits();
        for (SearchHit hit : hits) {
            Map<String, Object> sourceAsMap = hit.getSourceAsMap();
            //获取map集合中的数据存入List中
            CoursePub coursePub = new CoursePub();
            String name = sourceAsMap.get("name").toString();
            //获取文档中的高亮字段的map集合
            Map<String, HighlightField> highlightFields = hit.getHighlightFields();
            if (highlightFields!=null) {
                //获取name字段的高亮对象
                HighlightField hname = highlightFields.get("name");
                if (hname!=null) {
                    //获取name高亮对象中的所有符合关键字的拼接了标签的内容
                    Text[] fragments = hname.getFragments();
                    StringBuffer stringBuffer=new StringBuffer();
                    for (Text text : fragments) {
                        stringBuffer.append(text);
                    }
                    name=stringBuffer.toString();
                }
            }
            String id = sourceAsMap.get("id").toString();
            coursePub.setId(id);
            coursePub.setName(name);
            if(sourceAsMap.get("pic")!=null){
                coursePub.setPic(sourceAsMap.get("pic").toString());
            }
            if (StringUtils.isEmpty(sourceAsMap.get("price").toString())||StringUtils.isEmpty(sourceAsMap.get("price_old").toString())){
                ExceptionCast.cast(CourseCode.COURSE_PRICE_IS_NULL);
            }
            coursePub.setPrice((Double)sourceAsMap.get("price"));
            coursePub.setPrice_old((Double)sourceAsMap.get("price_old"));
            pubArrayList.add(coursePub);
        }
        queryResult.setList(pubArrayList);
        queryResult.setTotal(response.getHits().getTotalHits());
        return new QueryResponseResult<CoursePub>(CommonCode.SUCCESS,queryResult);
    }

    public Map<String, CoursePub> getall(String id) {
        //1.创建搜索请求对象
        SearchRequest searchRequest=new SearchRequest("xc_course");
        searchRequest.types("doc");
        //构建搜索源
        SearchSourceBuilder searchSourceBuilder=new SearchSourceBuilder();
        TermQueryBuilder termQueryBuilder = QueryBuilders.termQuery("id", id);
        searchSourceBuilder.query(termQueryBuilder);
        searchRequest.source(searchSourceBuilder);
        //查询
        try {
            Map<String,CoursePub> map=null;
            SearchResponse response = highLevelClient.search(searchRequest);
            SearchHits hits = response.getHits();
            if (hits==null){
                ExceptionCast.cast(CommonCode.QUERY_FAIL);
            }
            SearchHit[] hits1 = hits.getHits();
            for (SearchHit documentFields : hits1) {
                map=new HashMap<>();
                Map<String, Object> sourceAsMap = documentFields.getSourceAsMap();
                String courseId = (String) sourceAsMap.get("id");
                String name = (String) sourceAsMap.get("name");
                String grade = (String) sourceAsMap.get("grade");
                String charge = (String) sourceAsMap.get("charge");
                String pic = (String) sourceAsMap.get("pic");
                String description = (String) sourceAsMap.get("description");
                String teachplan = (String) sourceAsMap.get("teachplan");
                CoursePub coursePub = new CoursePub();
                coursePub.setId(courseId);
                coursePub.setName(name);
                coursePub.setPic(pic);
                coursePub.setGrade(grade);
                coursePub.setCharge(charge);
                coursePub.setTeachplan(teachplan);
                coursePub.setDescription(description);
                map.put(courseId,coursePub);
            }
            return map;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 根据课程计划查询媒资信息  一次查询多个id
     */
    public QueryResponseResult<TeachplanMediaPub> getmedia(String[] ids){
        //创建搜索请求对象
        SearchRequest searchRequest=new SearchRequest(media_index);
        searchRequest.types(media_type);
        //构建搜索源
        SearchSourceBuilder searchSourceBuilder=new SearchSourceBuilder();
        //设置显示字段
        String[] split = media_source_field.split(",");
        searchSourceBuilder.fetchSource(split,new String[]{});
        searchSourceBuilder.query(QueryBuilders.termsQuery("teachplan_id",ids));
        searchRequest.source(searchSourceBuilder);
        //查询
        long total=0;
        List<TeachplanMediaPub> list=new ArrayList();
        try {
            SearchResponse search = highLevelClient.search(searchRequest);
            SearchHits hits = search.getHits();
            if (hits==null){
                ExceptionCast.cast(CommonCode.QUERY_FAIL);
            }
            total=hits.getTotalHits();
            SearchHit[] hitsHits = hits.getHits();
            for (SearchHit hitsHit : hitsHits) {
                Map<String, Object> sourceAsMap = hitsHit.getSourceAsMap();
                TeachplanMediaPub teachplanMediaPub=new TeachplanMediaPub();
                String courseid = (String) sourceAsMap.get("courseid");
                String media_id = (String) sourceAsMap.get("media_id");
                String media_url = (String) sourceAsMap.get("media_url");
                String teachplan_id = (String) sourceAsMap.get("teachplan_id");
                String media_fileoriginalname = (String) sourceAsMap.get("media_fileoriginalname");

                teachplanMediaPub.setCourseId(courseid);
                teachplanMediaPub.setMediaUrl(media_url);
                teachplanMediaPub.setMediaFileOriginalName(media_fileoriginalname);
                teachplanMediaPub.setMediaId(media_id);
                teachplanMediaPub.setTeachplanId(teachplan_id);
                list.add(teachplanMediaPub);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        QueryResult queryResult=new QueryResult();
        queryResult.setList(list);
        queryResult.setTotal(total);
        return new QueryResponseResult(CommonCode.SUCCESS,queryResult);
    }
}
