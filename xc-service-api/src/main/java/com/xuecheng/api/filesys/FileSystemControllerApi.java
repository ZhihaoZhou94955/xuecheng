package com.xuecheng.api.filesys;

import com.xuecheng.framework.domain.filesystem.response.FileSystemCode;
import com.xuecheng.framework.domain.filesystem.response.UploadFileResult;
import com.xuecheng.framework.model.response.ResponseResult;
import org.springframework.web.multipart.MultipartFile;

public interface FileSystemControllerApi {
    public UploadFileResult uploadFile(MultipartFile multipartFile,String businesskey,String filetag,String metadata);
}