package com.xuecheng.order.mq;

import com.xuecheng.framework.domain.task.XcTask;
import com.xuecheng.order.service.TaskService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.Calendar;
import java.util.Date;
import java.util.List;

@Component
@Slf4j
public class taskTest {

    @Autowired
    private TaskService taskService;
    @Autowired
    private RabbitTemplate rabbitTemplate;
    // 第一位   秒  第二位  分钟  第三位  小时  第四位   月中的天  第五位  月  第六位 周中的天
   // @Scheduled(cron = "0/3 * * * * *")  //每隔 3秒执行一次
    public void task1() {
        log.info("===============测试定时任务1开始===============");
        try {
            Thread.sleep(5000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        log.info("===============测试定时任务1结束===============");
    }

    //@Scheduled(cron = "0/3 * * * * *")  //每隔 3秒执行一次
    public void task2() {
        log.info("===============测试定时任务2开始===============");
        try {
            Thread.sleep(5000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        log.info("===============测试定时任务2结束===============");
    }

    // 查询一分钟之前的 前N条记录  查出来之后 发送消息
    @Scheduled(cron = "0 0/1 * * * *")
    public  void   findByUpdateTimeBefore (){
        Calendar instance = Calendar.getInstance();
        instance.add(Calendar.MINUTE,-1);
        Date time = instance.getTime();
        List<XcTask> taskList = taskService.findTaskList(10, time);
        for (XcTask xcTask : taskList) {
           if (taskService.updateVersion(xcTask.getId(),xcTask.getVersion())>0){
               sendMessage(xcTask);
           }
        }
    }


    //发送消息  发送消息需要 routingkey  exchange  message
    private  void  sendMessage(XcTask xcTask){
        String mqRoutingkey = xcTask.getMqRoutingkey();
        String mqExchange = xcTask.getMqExchange();
        rabbitTemplate.convertAndSend(mqExchange,mqRoutingkey,xcTask);
        taskService.updateTaskTime(xcTask.getId(),new Date());

    }
}
