package com.xuecheng.manage_cms.config;

import com.mongodb.MongoClient;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.gridfs.GridFSBucket;
import com.mongodb.client.gridfs.GridFSBuckets;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class MongoConfig {

    @Value("${spring.data.mongodb.database}")
    private  String db;

    @Bean
    public GridFSBucket  gridFSBucket(MongoClient mongoClient){
        //1. 连接mongodb数据库
        MongoDatabase database = mongoClient.getDatabase(db);
        //2. 打开mongodb下载流
        GridFSBucket gridFSBucket = GridFSBuckets.create(database);
        return  gridFSBucket;
    }
}
