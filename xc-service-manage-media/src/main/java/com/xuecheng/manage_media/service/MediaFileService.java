package com.xuecheng.manage_media.service;

import com.xuecheng.framework.domain.media.MediaFile;
import com.xuecheng.framework.domain.media.request.QueryMediaFileRequest;
import com.xuecheng.framework.exception.ExceptionCast;
import com.xuecheng.framework.model.response.CommonCode;
import com.xuecheng.framework.model.response.QueryResponseResult;
import com.xuecheng.framework.model.response.QueryResult;
import com.xuecheng.manage_media.dao.MediaFileRepository;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class MediaFileService {

    @Autowired
    private MediaFileRepository mediaFileRepository;

    public QueryResponseResult findList(int page, int size, QueryMediaFileRequest queryMediaFileRequest) {
        MediaFile mediaFile = new MediaFile();
        //先判断参数是否为空
        if (StringUtils.isNotEmpty(queryMediaFileRequest.getFileOriginalName())){
            mediaFile.setFileOriginalName(queryMediaFileRequest.getFileOriginalName());
        }
        if (StringUtils.isNotEmpty(queryMediaFileRequest.getProcessStatus())){
            mediaFile.setProcessStatus(queryMediaFileRequest.getProcessStatus());
        }
        if (StringUtils.isNotEmpty(queryMediaFileRequest.getTag())){
            mediaFile.setTag(queryMediaFileRequest.getTag());
        }

        //创建 查询条件匹配器
        ExampleMatcher exampleMatcher=ExampleMatcher.matching().withMatcher("fileOriginalName", ExampleMatcher.GenericPropertyMatchers.contains())
                .withMatcher("tag", ExampleMatcher.GenericPropertyMatchers.contains()).withMatcher("processStatus", ExampleMatcher.GenericPropertyMatchers.exact());
        //通过Example.of  传入 条件对象 传入 查询条件匹配器
        Example<MediaFile> example = Example.of(mediaFile, exampleMatcher);
        //创建分页对象
        if (page<=0){
            page=0;
        }
        else {
            page=page-1;
        }
        Pageable pageable= PageRequest.of(page,size);
        //查询所有
        Page<MediaFile> mediaFiles = mediaFileRepository.findAll(example, pageable);
        if (mediaFiles==null){
            ExceptionCast.cast(CommonCode.QUERY_FAIL);
        }
        QueryResult<MediaFile> mediaFileQueryResult = new QueryResult<>();
        mediaFileQueryResult.setTotal(mediaFiles.getTotalElements());
        List<MediaFile> content = mediaFiles.getContent();
        mediaFileQueryResult.setList(content);
        return new QueryResponseResult(CommonCode.SUCCESS,mediaFileQueryResult);
    }
}
