package com.xuecheng.manage_cms.dao;

import com.mongodb.client.gridfs.GridFSBucket;
import com.mongodb.client.gridfs.GridFSDownloadStream;
import com.mongodb.client.gridfs.model.GridFSFile;
import org.apache.commons.io.IOUtils;
import org.bson.types.ObjectId;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.gridfs.GridFsResource;
import org.springframework.data.mongodb.gridfs.GridFsTemplate;
import org.springframework.test.context.junit4.SpringRunner;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;

@SpringBootTest
@RunWith(SpringRunner.class)
public class GridFsTest {

    @Autowired
    private GridFsTemplate gridFsTemplate;

    @Autowired
    private GridFSBucket gridFSBucket;
    @Test
    public  void Storetest() throws IOException {
        //1. 设置 模板所在路径
        File file = new File("I:\\CMS\\xcservice01\\fmtest\\src\\main\\resources\\templates\\course.ftl");
        //2. 通过输入流将模板内容读取到内存
        FileInputStream fileInputStream = new FileInputStream(file);
        //3. 将模板内容保存到GridFs中
        ObjectId objectId = gridFsTemplate.store(fileInputStream, "course.ftl");
        //4. 返回文件Id  关闭流
        System.out.println(objectId.toString());
        fileInputStream.close();

    }

    @Test
    public  void ReadTest() throws IOException {
        //创建查询条件 通过Query.query()   传入一个Criteria
        Query query = Query.query(Criteria.where("_id").is("5e6f67a591e75e2c44e5243a"));
        GridFSFile gridFSFile = gridFsTemplate.findOne(query);

        //打开一个下载流对象
        GridFSDownloadStream stream = gridFSBucket.openDownloadStream(gridFSFile.getObjectId());
        //获取流
        GridFsResource gridFsResource = new GridFsResource(gridFSFile,stream);
        String s = IOUtils.toString(gridFsResource.getInputStream(), "utf-8");
        System.out.println(s);
    }
}
