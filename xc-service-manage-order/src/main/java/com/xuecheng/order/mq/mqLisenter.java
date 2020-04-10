package com.xuecheng.order.mq;

import com.xuecheng.framework.domain.task.XcTask;
import com.xuecheng.order.service.TaskService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class mqLisenter {

    @Autowired
    private TaskService taskService;

    @RabbitListener(queues = "xc_learning_finishaddchoosecourse")
    public  void  receiveFinishChoosecourseTask(XcTask xcTask){
        try {
            taskService.finishTask(xcTask);
            log.info("【订单系统】 订单服务结束！");
        } catch (Exception e) {
            e.printStackTrace();
            log.error("【订单系统】 订单服务结束失败！");
        }

    }
}
