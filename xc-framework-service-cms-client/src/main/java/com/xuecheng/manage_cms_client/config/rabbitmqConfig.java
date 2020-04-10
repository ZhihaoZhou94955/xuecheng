package com.xuecheng.manage_cms_client.config;

import org.springframework.amqp.core.*;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class rabbitmqConfig {

    @Value("${xuecheng.mq.queue}")
    private  String queue;
    @Value("${xuecheng.mq.routingKey}")
    private  String routingKey;
    public static final String QUEUE_CMS_POSTPAGE = "queue_cms_postpage";
    public static final String EX_ROUTING_CMS_POSTPAGE="ex_routing_cms_postpage";

    @Bean(QUEUE_CMS_POSTPAGE)
    public Queue queue(){
        return  new Queue(queue,true);
    }

    @Bean(EX_ROUTING_CMS_POSTPAGE)
    public Exchange exchange(){
        Exchange exchange = ExchangeBuilder.directExchange(EX_ROUTING_CMS_POSTPAGE).durable(true).build();
        return exchange;
    }

    @Bean
    public Binding binding(@Qualifier(QUEUE_CMS_POSTPAGE) Queue queue,@Qualifier(EX_ROUTING_CMS_POSTPAGE) Exchange exchange){
        Binding binding = BindingBuilder.bind(queue).to(exchange).with(routingKey).noargs();
        return binding;
    }
}
