package com.xuecheng.manage_cms.config;

import org.springframework.amqp.core.*;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class rabbitmqConfig {




    public static final String EX_ROUTING_CMS_POSTPAGE="ex_routing_cms_postpage";

    @Bean(EX_ROUTING_CMS_POSTPAGE)
    public Exchange exchange(){
        Exchange exchange = ExchangeBuilder.directExchange(EX_ROUTING_CMS_POSTPAGE).durable(true).build();
        return exchange;
    }

}
