package com.xuecheng.framework.exception;

import com.google.common.collect.ImmutableMap;
import com.xuecheng.framework.model.response.CommonCode;
import com.xuecheng.framework.model.response.ResponseResult;
import com.xuecheng.framework.model.response.ResultCode;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestControllerAdvice;

/**
 * 自定义异常捕获类
 */

@RestControllerAdvice  //控制器增强
@Slf4j
public class ExceptionCatch {

    @ExceptionHandler(CustomException.class)
    //捕捉自定义异常
    public ResponseResult Catch(CustomException customexception){
        ResultCode resultCode = customexception.getResultCode();
        log.error("捕获异常：{}", resultCode.message());
        return new ResponseResult(resultCode);
    }

                        // immutableMap 的key的条件是 继承了Throwable的异常类  value是 错误代码
    private static ImmutableMap<Class<? extends Throwable>,ResultCode> immutableMap;

    protected static ImmutableMap.Builder<Class<? extends Throwable>,ResultCode> builder=ImmutableMap.builder();
    //捕捉不可预知异常 假如抛出的异常是CustomException 异常处理器会执行 Catch方法 因为它的捕捉异常范围更精确
    @ExceptionHandler(Exception.class)
    public  ResponseResult catchException(Exception exception) {
        String message = exception.getMessage();
        if (immutableMap == null) {
            immutableMap = builder.build();
        }
        ResultCode resultCode = immutableMap.get(exception.getClass());
        if (resultCode==null){
            return  new ResponseResult(CommonCode.SERVER_ERROR);
        }
        else {
            log.error("捕获异常：{}", resultCode.message());
            //捕捉到不可预知异常后，需要返回精确的信息
            return  new ResponseResult(resultCode);
        }
    }

    static {
        //将一些不可预知的异常 提前加入到builder中
        builder.put(HttpMessageNotReadableException.class,CommonCode.INVALID_PARAM);
    }
}
