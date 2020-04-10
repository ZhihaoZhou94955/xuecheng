package com.xuecheng.govern.gateway.Filter;

import com.alibaba.fastjson.JSON;
import com.netflix.zuul.ZuulFilter;
import com.netflix.zuul.context.RequestContext;
import com.netflix.zuul.exception.ZuulException;
import com.xuecheng.framework.model.response.CommonCode;
import com.xuecheng.framework.model.response.ResponseResult;
import org.apache.commons.lang3.StringUtils;
import org.springframework.context.annotation.FilterType;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Map;

//@Component
public class TestFilter extends ZuulFilter {
    //执行的时机
    @Override
    public String filterType() {
        return "pre";
    }

    //执行顺序  越小越优先执行
    @Override
    public int filterOrder() {
        return 0;
    }
    //是否拦截
    @Override
    public boolean shouldFilter() {
        return true;  //true 是拦截
    }

    // 将请求拦截之后要进行的操作
    @Override
    public Object run() throws ZuulException {
        RequestContext currentContext = RequestContext.getCurrentContext();
        HttpServletRequest request = currentContext.getRequest();
        HttpServletResponse response = currentContext.getResponse();
        //判断 头信息 是否 有 authorization
        if (StringUtils.isEmpty(request.getHeader("Authorization"))){
            //如果为空  拒绝访问
            currentContext.setSendZuulResponse(false); //不放行
            ResponseResult unauthenticated = new ResponseResult(CommonCode.UNAUTHENTICATED);
            String s = JSON.toJSONString(unauthenticated);
            currentContext.setResponseBody(s);
            //设置 返回类型
            response.setContentType("application/json;charset=utf-8");
            return null;
        }
        return null;
    }
}
