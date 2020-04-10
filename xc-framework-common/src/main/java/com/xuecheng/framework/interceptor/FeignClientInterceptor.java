package com.xuecheng.framework.interceptor;

import feign.RequestInterceptor;
import feign.RequestTemplate;
import org.springframework.util.StringUtils;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;
import java.util.Enumeration;

public class FeignClientInterceptor implements RequestInterceptor {

    /**
     * 因为 微服务整合了 oauth2 所以 微服务之间调用也要传令牌
     * @param template
     */
    @Override
    public void apply(RequestTemplate template) {
        try {
            ServletRequestAttributes requestAttributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
            if (requestAttributes!=null){
                HttpServletRequest request = requestAttributes.getRequest();
                Enumeration<String> headerNames = request.getHeaderNames();
                if (headerNames!=null){
                    //如果还有下一个元素
                    while (headerNames.hasMoreElements()){
                        //获取名称
                        String name = headerNames.nextElement();
                        //通过名称获取值
                        String header = request.getHeader(name);
                        if (name.equals("authorization")){
                            template.header(name,header);
                        }
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
