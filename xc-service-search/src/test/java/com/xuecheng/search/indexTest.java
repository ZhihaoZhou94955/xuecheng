package com.xuecheng.search;

import com.alibaba.fastjson.JSON;
import org.elasticsearch.action.DocWriteResponse;
import org.elasticsearch.action.admin.indices.create.CreateIndexRequest;
import org.elasticsearch.action.admin.indices.create.CreateIndexResponse;
import org.elasticsearch.action.admin.indices.delete.DeleteIndexRequest;
import org.elasticsearch.action.admin.indices.delete.DeleteIndexResponse;
import org.elasticsearch.action.delete.DeleteRequest;
import org.elasticsearch.action.delete.DeleteResponse;
import org.elasticsearch.action.get.GetRequest;
import org.elasticsearch.action.get.GetResponse;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.action.index.IndexResponse;
import org.elasticsearch.action.support.master.AcknowledgedResponse;
import org.elasticsearch.action.update.UpdateRequest;
import org.elasticsearch.action.update.UpdateResponse;
import org.elasticsearch.client.IndicesClient;
import org.elasticsearch.client.RestClient;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.common.xcontent.XContentType;
import org.elasticsearch.rest.RestStatus;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@SpringBootTest
@RunWith(SpringRunner.class)
public class indexTest {

    @Autowired
    private RestHighLevelClient restHighLevelClient;

    @Autowired
    private RestClient restClient;

    @Test
    public  void CreateIndex() throws IOException {
        //1.创建索引库
        CreateIndexRequest createIndexRequest=new CreateIndexRequest("xc_course");
        //2. 设置分片信息和副本信息
        createIndexRequest.settings(Settings.builder().put("number_of_shards",1).put("number_of_replicas",0));
        //3. 添加映射   索引库类型 doc
        createIndexRequest.mapping("doc","{\n" +
                "\"properties\": {\n" +
                "\"name\": {\n" +
                "\"type\": \"text\",\n" +
                "\"analyzer\":\"ik_max_word\",\n" +
                "\"search_analyzer\":\"ik_smart\"\n" +
                "},\n" +
                "\"description\": {\n" +
                "\"type\": \"text\",\n" +
                "\"analyzer\":\"ik_max_word\",\n" +
                "\"search_analyzer\":\"ik_smart\"\n" +
                "},\n" +
                "\"studymodel\": {\n" +
                "\"type\": \"keyword\"\n" +
                "},\n" +
                "\"price\": {\n" +
                "\"type\": \"float\"\n" +
                "}\n" +
                "}\n" +
                "}", XContentType.JSON);
        //4. 通过 restHighLevelClient 获取 操作索引客户端
        IndicesClient indicesClient=restHighLevelClient.indices();
        //5. 通过 操作索引客户端  创建索引库
        CreateIndexResponse createIndexResponse=indicesClient.create(createIndexRequest);
        System.out.println(createIndexResponse.isAcknowledged());

    }

    @Test
    public  void  deleteIndex() throws IOException {
        DeleteIndexRequest deleteIndexRequest = new DeleteIndexRequest("xc_course");
//删除索引
        DeleteIndexResponse deleteIndexResponse = restHighLevelClient.indices().delete(deleteIndexRequest);
//删除索引响应结果
        boolean acknowledged = deleteIndexResponse.isAcknowledged();
        System.out.println(acknowledged);
    }

    @Test
    public  void  FindDocument() throws IOException {
            //创建 查询索引请求对象
        //第一个参数  要搜索的文档的索引库名称  第二个参数 索引库的类型  第三个参数 文档的id
        GetRequest getRequest=new GetRequest("xc_course","doc","596272756");
        //通过 resthighlevelclient的get方法去查询文档
        GetResponse documentFields = restHighLevelClient.get(getRequest);
        boolean exists = documentFields.isExists();
        Map<String, Object> sourceAsMap = documentFields.getSourceAsMap();
        System.out.println(sourceAsMap);
    }

    @Test
    public  void  addDocument() throws IOException {
        //创建 添加索引请求对象
        IndexRequest indexRequest=new IndexRequest("xc_course","doc","596272756");
        //准备数据
        Map<String, Object> jsonMap = new HashMap<>();
        jsonMap.put("name", "spring cloud实战");
        jsonMap.put("description", "本课程主要从四个章节进行讲解： 1.微服务架构入门 2.spring cloud 基础入门 3.实战Spring Boot 4.注册中心eureka。");
        jsonMap.put("studymodel", "201001");
        SimpleDateFormat dateFormat =new SimpleDateFormat("yyyy‐MM‐dd HH:mm:ss");
        jsonMap.put("timestamp", dateFormat.format(new Date()));
        jsonMap.put("price", 5.6f);
        //将map集合传入 indexRequest中
        indexRequest.source(jsonMap);
        //通过 resthighlevelclient的index方法 添加 文档
        IndexResponse index = restHighLevelClient.index(indexRequest);
        DocWriteResponse.Result result = index.getResult();
        System.out.println(result);

    }

    @Test
    public  void deleteDocument() throws IOException {
        //创建 删除索引请求对象
        //第一个参数 要删除的文档所在的索引库名称 ， 第二个参数 索引库类型  第三个 参数 要删除的文档的id
        DeleteRequest deleteRequest = new DeleteRequest("xc_course","doc","596272756");

        DeleteResponse deleteResponse=restHighLevelClient.delete(deleteRequest);
        DocWriteResponse.Result result = deleteResponse.getResult();
        System.out.println(result);

    }

    @Test
    public  void  updateDocument() throws IOException {
        //创建  更新索引请求对象
        UpdateRequest updateRequest= new UpdateRequest("xc_course","doc","596272756");
        //准备要更新的数据的信息的json格式
        Map<String, String> map = new HashMap<>();
        map.put("name", "java实战");
        //将准备好的数据 存入 更新索引请求对象
        updateRequest.doc(map);
        //执行更新
        UpdateResponse update=restHighLevelClient.update(updateRequest);
        RestStatus status = update.status();
        System.out.println(status);
    }
}
