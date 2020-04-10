package com.xuecheng.framework.domain.learning.responses;

import com.google.common.collect.ImmutableMap;
import com.xuecheng.framework.model.response.ResultCode;
import io.swagger.annotations.ApiModelProperty;
import lombok.ToString;


/**
 * Created by admin on 2018/3/5.
 */
@ToString
public enum LearningCode implements ResultCode {

    LEARNING_GETMEDIA_ERROR(false,33000,"获取视频播放地址出错");
    //操作代码

    boolean success;

    //操作代码
    int code;
    //提示信息
    String message;
    private LearningCode(boolean success, int code, String message){
        this.success = success;
        this.code = code;
        this.message = message;
    }
    private static final ImmutableMap<Integer, LearningCode> CACHE;

    static {
        final ImmutableMap.Builder<Integer, LearningCode> builder = ImmutableMap.builder();
        for (LearningCode commonCode : values()) {
            builder.put(commonCode.code(), commonCode);
        }
        CACHE = builder.build();
    }
    @Override
    public boolean success() {
        return success;
    }

    @Override
    public int code() {
        return code;
    }

    @Override
    public String message() {
        return message;
    }
}
