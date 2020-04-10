package com.xuecheng.manage_course.exception;

import com.xuecheng.framework.exception.ExceptionCatch;
import com.xuecheng.framework.model.response.CommonCode;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.web.bind.annotation.ControllerAdvice;

@ControllerAdvice
public class courseException extends ExceptionCatch {

    static {
        //将一些不可预知的异常 提前加入到builder中
        builder.put(AccessDeniedException.class, CommonCode.UNAUTHORISE);
    }

}
