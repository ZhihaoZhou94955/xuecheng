package com.xuecheng.govern.gateway.Filter;

import com.alibaba.fastjson.JSON;
import com.netflix.zuul.ZuulFilter;
import com.netflix.zuul.context.RequestContext;
import com.netflix.zuul.exception.ZuulException;
import com.xuecheng.framework.model.response.CommonCode;
import com.xuecheng.framework.model.response.ResponseResult;
import com.xuecheng.govern.gateway.service.AuthService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import static org.bouncycastle.crypto.tls.AlertDescription.access_denied;

@Component
public class LoginFilter extends ZuulFilter {

    @Autowired
    private AuthService authService;
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
        //1.通过cookie 获取 身份令牌 如果 没有  拒绝访问
        RequestContext currentContext = RequestContext.getCurrentContext();
        HttpServletRequest request = currentContext.getRequest();
        String access_token = authService.getTokenFromCookie(request);
        if (access_token==null){
            access_denied();
        }

        //2. 通过头信息 获取 jwt令牌 如果没有  拒绝访问
        String jwtFromHeader = authService.getJwtFromHeader(request);
        if (jwtFromHeader==null){
            access_denied();
        }

        //3. 通过身份令牌去 redis查询  如果 redis中不存在  拒绝访问
        long expire = authService.getExpire(access_token);
        if (expire<=0){
            access_denied();
        }
        //否则放行
        return null;
    }

    private void  access_denied(){
        RequestContext currentContext = RequestContext.getCurrentContext();
        HttpServletResponse response = currentContext.getResponse();
        //如果为空  拒绝访问
        currentContext.setSendZuulResponse(false); //不放行
        ResponseResult unauthenticated = new ResponseResult(CommonCode.UNAUTHENTICATED);
        String s = JSON.toJSONString(unauthenticated);
        currentContext.setResponseBody(s);
        //设置 返回类型
        response.setContentType("application/json;charset=utf-8");
    }
}
