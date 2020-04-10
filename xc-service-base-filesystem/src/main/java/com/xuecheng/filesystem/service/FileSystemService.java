package com.xuecheng.filesystem.service;

import com.alibaba.fastjson.JSON;
import com.xuecheng.filesystem.dao.FileSystemRepositry;
import com.xuecheng.framework.domain.filesystem.FileSystem;
import com.xuecheng.framework.domain.filesystem.response.FileSystemCode;
import com.xuecheng.framework.domain.filesystem.response.UploadFileResult;
import com.xuecheng.framework.exception.ExceptionCast;
import com.xuecheng.framework.model.response.CommonCode;
import org.apache.commons.lang3.StringUtils;
import org.csource.common.MyException;
import org.csource.fastdfs.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Map;

@Service
public class FileSystemService {
    @Autowired
    private FileSystemRepositry fileSystemRepositry;

    @Value("${xuecheng.fastdfs.tracker_servers}")
    private String tracker_servers;
    @Value("${xuecheng.fastdfs.connect_timeout_in_seconds}")
    private int connect_timeout_in_seconds;
    @Value("${xuecheng.fastdfs.network_timeout_in_seconds}")
    private int network_timeout_in_seconds;
    @Value("${xuecheng.fastdfs.charset}")
    private String charset;

    public UploadFileResult uploadFile(MultipartFile multipartFile, String businesskey, String filetag, String metadata){
        if (multipartFile==null){
            return new UploadFileResult(FileSystemCode.FS_UPLOADFILE_FILEISNULL,null);
        }
        //1. 初始化信息
        this.init();
        //2. 将文件上传到FastDFS
        String fileId = this.uploadFiletoFDFS(multipartFile);
        if (fileId==null){
            return new UploadFileResult(FileSystemCode.FS_UPLOADFILE_SERVERFAIL,null);
        }
        //3. 将上传的文件信息保存到mongodb中
        FileSystem fileSystem = this.saveToMongoDB(multipartFile, businesskey, filetag, metadata, fileId);

        return new UploadFileResult(CommonCode.SUCCESS,fileSystem);
    }

    private FileSystem saveToMongoDB(MultipartFile multipartFile, String businesskey, String filetag, String metadata, String fileId) {
        String filename = multipartFile.getOriginalFilename();
        FileSystem fileSystem = new FileSystem();
        fileSystem.setBusinesskey(businesskey);
        fileSystem.setFiletag(filetag);
        Map map = JSON.parseObject(metadata, Map.class);
        fileSystem.setMetadata(map);
        fileSystem.setFileId(fileId);
        fileSystem.setFilePath(fileId);
        fileSystem.setFileName(filename);
        fileSystem.setFileSize(multipartFile.getSize());
        fileSystem.setFileType(multipartFile.getContentType());
        FileSystem system = fileSystemRepositry.save(fileSystem);
        if (system==null){
            ExceptionCast.cast(CommonCode.INSERT_FAIL);

        }
        return fileSystem;
    }

    private void init() {
        try {
            ClientGlobal.initByTrackers(tracker_servers);
            ClientGlobal.setG_charset(charset);
            ClientGlobal.setG_connect_timeout(connect_timeout_in_seconds);
            ClientGlobal.setG_network_timeout(network_timeout_in_seconds);
        } catch (IOException e) {
            e.printStackTrace();
        } catch (MyException e) {
            e.printStackTrace();
        }
    }

    public String uploadFiletoFDFS(MultipartFile multipartFile){
        try {
            //1. 创建 trackerClient
            TrackerClient trackerClient = new TrackerClient();
            //2. 获取trackerServer
            TrackerServer connection = trackerClient.getConnection();
            //3. 获取storageServer
            StorageServer storeStorage = trackerClient.getStoreStorage(connection);
            //4. 创建storageClient
            StorageClient1 storageClient1 = new StorageClient1(connection,storeStorage);
            //5. 上传文件
                //获取文件名称
            String filename = multipartFile.getOriginalFilename();
                //获取文件扩展名  获取文件名称中的最后一个点的位置再+1 就是扩展名称
            String extraname = filename.substring(filename.lastIndexOf('.') + 1);
            byte[] bytes = multipartFile.getBytes();
            String fileId = storageClient1.upload_file1(bytes, extraname, null);
            return fileId;
        } catch (IOException e) {
            e.printStackTrace();
        } catch (MyException e) {
            e.printStackTrace();
        }
        return null;
    }
}
