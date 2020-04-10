package com.xuecheng.search;

import org.apache.lucene.search.highlight.Highlighter;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.RestClient;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.index.query.*;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.SearchHits;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.elasticsearch.search.fetch.subphase.highlight.HighlightBuilder;
import org.elasticsearch.search.fetch.subphase.highlight.HighlightField;
import org.elasticsearch.search.fetch.subphase.highlight.HighlightUtils;
import org.elasticsearch.search.sort.FieldSortBuilder;
import org.elasticsearch.search.sort.SortBuilders;
import org.elasticsearch.search.sort.SortOrder;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.io.IOException;
import java.util.Map;

@SpringBootTest
@RunWith(SpringRunner.class)
public class DSLTest {

    @Autowired
    private RestHighLevelClient restHighLevelClient;

    @Autowired
    private RestClient restClient;

    /**
     * 查询所有文档
     */
    @Test
    public  void  QueryALL() throws IOException {
        //1. 创建搜索请求对象
        SearchRequest searchRequest=new SearchRequest("xc_course");
        //2. 指定索引库类型
        searchRequest.types("doc");
        //3. 构建搜索源
        SearchSourceBuilder searchSourceBuilder=new SearchSourceBuilder();
        //4. 设置 查询的范围
        searchSourceBuilder.query(QueryBuilders.matchAllQuery());
        //5. 设置过滤的字段
        searchSourceBuilder.fetchSource(new String[]{"name","studymodel","price"},new String[]{"description"});
        //6. 将 搜索源 存入 搜索请求对象中
        searchRequest.source(searchSourceBuilder);
        //7. 调用client的search方法 进行查询  返回  SearchResponse对象
        SearchResponse response = restHighLevelClient.search(searchRequest);
        //8. 打印
        SearchHits hits = response.getHits();//获取搜索到的文档
        SearchHit[] searchHits = hits.getHits();
        for (SearchHit searchHit : searchHits) {
            //将文档的内容转换成 map集合
            Map<String, Object> sourceAsMap = searchHit.getSourceAsMap();
            System.out.println(sourceAsMap);
        }

    }
    @Test
    public  void  Paingquery() throws IOException {
        //1. 创建搜索请求对象
        SearchRequest searchRequest=new SearchRequest("xc_course");
        //2. 指定索引库类型
        searchRequest.types("doc");
        //3. 构建搜索源
        SearchSourceBuilder searchSourceBuilder=new SearchSourceBuilder();
        //4. 设置 查询的范围
        // 设置 分页起始位置
        int page=1;
        int size=2;
        //from是 起始的记录数的位置 不是页数
        searchSourceBuilder.from(size*(page-1));
        searchSourceBuilder.size(size);
        searchSourceBuilder.query(QueryBuilders.matchAllQuery());
        //5. 设置过滤的字段
        searchSourceBuilder.fetchSource(new String[]{"name","studymodel","price"},new String[]{"description"});
        //6. 将 搜索源 存入 搜索请求对象中
        searchRequest.source(searchSourceBuilder);
        //7. 调用client的search方法 进行查询  返回  SearchResponse对象
        SearchResponse response = restHighLevelClient.search(searchRequest);
        //8. 打印
        SearchHits hits = response.getHits();//获取搜索到的文档
        SearchHit[] searchHits = hits.getHits();
        for (SearchHit searchHit : searchHits) {
            //将文档的内容转换成 map集合
            Map<String, Object> sourceAsMap = searchHit.getSourceAsMap();
            System.out.println(sourceAsMap);
        }

    }

    @Test
    public  void  TermQuery() throws IOException {
        //1. 创建搜索请求对象
        SearchRequest searchRequest=new SearchRequest("xc_course");
        //2. 指定索引库类型
        searchRequest.types("doc");
        //3. 构建搜索源
        SearchSourceBuilder searchSourceBuilder=new SearchSourceBuilder();
        //4. 设置 查询的范围
        //termQuery 是 关键词查询 它是精准匹配  不会将查询的内容进行分析
        searchSourceBuilder.query(QueryBuilders.termQuery("name","spring"));
        //5. 设置过滤的字段
        searchSourceBuilder.fetchSource(new String[]{"name","studymodel","price"},new String[]{"description"});
        //6. 将 搜索源 存入 搜索请求对象中
        searchRequest.source(searchSourceBuilder);
        //7. 调用client的search方法 进行查询  返回  SearchResponse对象
        SearchResponse response = restHighLevelClient.search(searchRequest);
        //8. 打印
        SearchHits hits = response.getHits();//获取搜索到的文档
        SearchHit[] searchHits = hits.getHits();
        for (SearchHit searchHit : searchHits) {
            //将文档的内容转换成 map集合
            Map<String, Object> sourceAsMap = searchHit.getSourceAsMap();
            System.out.println(sourceAsMap);
        }

    }

    @Test
    public  void  QueryByIds() throws IOException {
        //1. 创建搜索请求对象
        SearchRequest searchRequest=new SearchRequest("xc_course");
        //2. 指定索引库类型
        searchRequest.types("doc");
        //3. 构建搜索源
        SearchSourceBuilder searchSourceBuilder=new SearchSourceBuilder();
        //4. 设置 查询的范围
        //通过 id数组 来查询对应的文档集合  需要使用termsQuery  而不是 termQuery
        String[] ids=new String[]{"1","2","3"};
        searchSourceBuilder.query(QueryBuilders.termsQuery("_id",ids));
        //5. 设置过滤的字段
        searchSourceBuilder.fetchSource(new String[]{"name","studymodel","price"},new String[]{"description"});
        //6. 将 搜索源 存入 搜索请求对象中
        searchRequest.source(searchSourceBuilder);
        //7. 调用client的search方法 进行查询  返回  SearchResponse对象
        SearchResponse response = restHighLevelClient.search(searchRequest);
        //8. 打印
        SearchHits hits = response.getHits();//获取搜索到的文档
        SearchHit[] searchHits = hits.getHits();
        for (SearchHit searchHit : searchHits) {
            //将文档的内容转换成 map集合
            Map<String, Object> sourceAsMap = searchHit.getSourceAsMap();
            System.out.println(sourceAsMap);
        }

    }
    @Test
    public  void  matchQuery() throws IOException {
        //1. 创建搜索请求对象
        SearchRequest searchRequest=new SearchRequest("xc_course");
        //2. 指定索引库类型
        searchRequest.types("doc");
        //3. 构建搜索源
        SearchSourceBuilder searchSourceBuilder=new SearchSourceBuilder();
        //4. 设置 查询的范围
        searchSourceBuilder.query(QueryBuilders.matchQuery("description","spring开发框架").minimumShouldMatch("70%"));
        //5. 设置过滤的字段
        searchSourceBuilder.fetchSource(new String[]{"name","studymodel","price"},new String[]{"description"});
        //6. 将 搜索源 存入 搜索请求对象中
        searchRequest.source(searchSourceBuilder);
        //7. 调用client的search方法 进行查询  返回  SearchResponse对象
        SearchResponse response = restHighLevelClient.search(searchRequest);
        //8. 打印
        SearchHits hits = response.getHits();//获取搜索到的文档
        SearchHit[] searchHits = hits.getHits();
        for (SearchHit searchHit : searchHits) {
            //将文档的内容转换成 map集合
            Map<String, Object> sourceAsMap = searchHit.getSourceAsMap();
            System.out.println(sourceAsMap);
        }

    }

    @Test
    public  void multiQuery() throws IOException {
        //1. 创建搜索请求对象
        SearchRequest searchRequest=new SearchRequest("xc_course");
        //2. 指定索引库类型
        searchRequest.types("doc");
        //3. 构建搜索源
        SearchSourceBuilder searchSourceBuilder=new SearchSourceBuilder();
        //4. 设置 查询的范围
        // multiQuery 可以同时查询多个字段   只要其中一个字段包含查询的内容 就会被查询出
        searchSourceBuilder.query(QueryBuilders.multiMatchQuery("spring css",new String[]{"name","description"}).minimumShouldMatch("50%").field("name",5));
        //5. 设置过滤的字段
        searchSourceBuilder.fetchSource(new String[]{"name","studymodel","price"},new String[]{"description"});
        //6. 将 搜索源 存入 搜索请求对象中
        searchRequest.source(searchSourceBuilder);
        //7. 调用client的search方法 进行查询  返回  SearchResponse对象
        SearchResponse response = restHighLevelClient.search(searchRequest);
        //8. 打印
        SearchHits hits = response.getHits();//获取搜索到的文档
        SearchHit[] searchHits = hits.getHits();
        for (SearchHit searchHit : searchHits) {
            //将文档的内容转换成 map集合
            Map<String, Object> sourceAsMap = searchHit.getSourceAsMap();
            System.out.println(sourceAsMap);
        }

    }

    @Test
    public  void boolQuery() throws IOException {
        //1. 创建搜索请求对象
        SearchRequest searchRequest=new SearchRequest("xc_course");
        //2. 指定索引库类型
        searchRequest.types("doc");
        //3. 构建搜索源
        SearchSourceBuilder searchSourceBuilder=new SearchSourceBuilder();
        //4. 设置 查询的范围
        //创建 multiQueryBuilder 查询name和description字段中包含 spring 或者 css 或者 spring css 同时包含的文档
        MultiMatchQueryBuilder matchQueryBuilder = QueryBuilders.multiMatchQuery("spring css", new String[]{"name", "description"}).minimumShouldMatch("50%").field("name", 5);
        //创建 termQueryBuilder  精确查询 studymodel
        TermQueryBuilder termQueryBuilder=QueryBuilders.termQuery("studymodel","201002");
        //创建boolquerybuilder 将两个查询条件合并到一起  因为两个条件都必须满足  所以要用must
        BoolQueryBuilder boolQueryBuilder=new BoolQueryBuilder();
        boolQueryBuilder.must(matchQueryBuilder);
        boolQueryBuilder.must(termQueryBuilder);
        searchSourceBuilder.query(boolQueryBuilder);
        //5. 设置过滤的字段
        searchSourceBuilder.fetchSource(new String[]{"name","studymodel","price"},new String[]{"description"});
        //6. 将 搜索源 存入 搜索请求对象中
        searchRequest.source(searchSourceBuilder);
        //7. 调用client的search方法 进行查询  返回  SearchResponse对象
        SearchResponse response = restHighLevelClient.search(searchRequest);
        //8. 打印
        SearchHits hits = response.getHits();//获取搜索到的文档
        SearchHit[] searchHits = hits.getHits();
        for (SearchHit searchHit : searchHits) {
            //将文档的内容转换成 map集合
            Map<String, Object> sourceAsMap = searchHit.getSourceAsMap();
            System.out.println(sourceAsMap);
        }

    }

    @Test
    public  void Filter() throws IOException {
        //1. 创建搜索请求对象
        SearchRequest searchRequest=new SearchRequest("xc_course");
        //2. 指定索引库类型
        searchRequest.types("doc");
        //3. 构建搜索源
        SearchSourceBuilder searchSourceBuilder=new SearchSourceBuilder();
        //4. 设置 查询的范围
        //创建 multiQueryBuilder 查询name和description字段中包含 spring 或者 css 或者 spring css 同时包含的文档
        MultiMatchQueryBuilder matchQueryBuilder = QueryBuilders.multiMatchQuery("spring css", new String[]{"name", "description"}).minimumShouldMatch("50%").field("name", 5);
        //创建 termQueryBuilder  精确查询 studymodel
//        TermQueryBuilder termQueryBuilder=QueryBuilders.termQuery("studymodel","201001");
        //创建boolquerybuilder 将两个查询条件合并到一起  因为两个条件都必须满足  所以要用must
        RangeQueryBuilder rangeQueryBuilder = QueryBuilders.rangeQuery("price").gt(40).lt(100);
        BoolQueryBuilder boolQueryBuilder=new BoolQueryBuilder();
        boolQueryBuilder.must(matchQueryBuilder);
//        searchSourceBuilder.query(boolQueryBuilder.filter(termQueryBuilder));
        searchSourceBuilder.query(boolQueryBuilder.filter(rangeQueryBuilder));
        //5. 设置过滤的字段
        searchSourceBuilder.fetchSource(new String[]{"name","studymodel","price"},new String[]{"description"});
        //6. 将 搜索源 存入 搜索请求对象中
        searchRequest.source(searchSourceBuilder);
        //7. 调用client的search方法 进行查询  返回  SearchResponse对象
        SearchResponse response = restHighLevelClient.search(searchRequest);
        //8. 打印
        SearchHits hits = response.getHits();//获取搜索到的文档
        SearchHit[] searchHits = hits.getHits();
        for (SearchHit searchHit : searchHits) {
            //将文档的内容转换成 map集合
            Map<String, Object> sourceAsMap = searchHit.getSourceAsMap();
            System.out.println(sourceAsMap);
        }

    }

    @Test
    public  void Sort() throws IOException {
        //1. 创建搜索请求对象
        SearchRequest searchRequest=new SearchRequest("xc_course");
        //2. 指定索引库类型
        searchRequest.types("doc");
        //3. 构建搜索源
        SearchSourceBuilder searchSourceBuilder=new SearchSourceBuilder();
        //4. 设置 查询的范围
        //创建 multiQueryBuilder 查询name和description字段中包含 spring 或者 css 或者 spring css 同时包含的文档
        MultiMatchQueryBuilder matchQueryBuilder = QueryBuilders.multiMatchQuery("spring css", new String[]{"name", "description"}).minimumShouldMatch("50%").field("name", 5);
        //创建 termQueryBuilder  精确查询 studymodel
        //创建boolquerybuilder 将两个查询条件合并到一起  因为两个条件都必须满足  所以要用must
        BoolQueryBuilder boolQueryBuilder=new BoolQueryBuilder();
        boolQueryBuilder.must(matchQueryBuilder);
        searchSourceBuilder.query(boolQueryBuilder);
        searchSourceBuilder.sort("price", SortOrder.DESC);
        //5. 设置过滤的字段
        searchSourceBuilder.fetchSource(new String[]{"name","studymodel","price"},new String[]{"description"});
        //6. 将 搜索源 存入 搜索请求对象中
        searchRequest.source(searchSourceBuilder);
        //7. 调用client的search方法 进行查询  返回  SearchResponse对象
        SearchResponse response = restHighLevelClient.search(searchRequest);
        //8. 打印
        SearchHits hits = response.getHits();//获取搜索到的文档
        SearchHit[] searchHits = hits.getHits();
        for (SearchHit searchHit : searchHits) {
            //将文档的内容转换成 map集合
            Map<String, Object> sourceAsMap = searchHit.getSourceAsMap();
            System.out.println(sourceAsMap);
        }

    }
    @Test
    public  void HighLight() throws IOException {
        //1. 创建搜索请求对象
        SearchRequest searchRequest=new SearchRequest("xc_course");
        //2. 指定索引库类型
        searchRequest.types("doc");
        //3. 构建搜索源
        SearchSourceBuilder searchSourceBuilder=new SearchSourceBuilder();
        //4. 设置 查询的范围
        //创建 multiQueryBuilder 查询name和description字段中包含 spring 或者 css 或者 spring css 同时包含的文档
        MultiMatchQueryBuilder matchQueryBuilder = QueryBuilders.multiMatchQuery("开发框架", new String[]{"name", "description"}).minimumShouldMatch("50%").field("name", 5);
        //创建 termQueryBuilder  精确查询 studymodel
        //创建boolquerybuilder 将两个查询条件合并到一起  因为两个条件都必须满足  所以要用must
        BoolQueryBuilder boolQueryBuilder=new BoolQueryBuilder();
        boolQueryBuilder.must(matchQueryBuilder);
        searchSourceBuilder.query(boolQueryBuilder);
        //设置高亮
        HighlightBuilder highlightBuilder=new HighlightBuilder();
        highlightBuilder.preTags("<tag>");
        highlightBuilder.postTags("</tag>");
        //设置要高亮的字段
        highlightBuilder.fields().add(new HighlightBuilder.Field("name"));
        searchSourceBuilder.highlighter(highlightBuilder);
        //5. 设置过滤的字段
        searchSourceBuilder.fetchSource(new String[]{"name","studymodel","price"},new String[]{"description"});
        //6. 将 搜索源 存入 搜索请求对象中
        searchRequest.source(searchSourceBuilder);
        //7. 调用client的search方法 进行查询  返回  SearchResponse对象
        SearchResponse response = restHighLevelClient.search(searchRequest);
        //8. 打印
        SearchHits hits = response.getHits();//获取搜索到的文档
        SearchHit[] searchHits = hits.getHits();
        for (SearchHit searchHit : searchHits) {
            //将文档的内容转换成 map集合
            Map<String, Object> sourceAsMap = searchHit.getSourceAsMap();
            System.out.println(sourceAsMap);
            Map<String, HighlightField> highlightFields = searchHit.getHighlightFields();
            System.out.println(highlightFields);
        }

    }
}
