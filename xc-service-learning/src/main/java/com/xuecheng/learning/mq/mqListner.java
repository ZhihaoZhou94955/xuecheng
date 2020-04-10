package com.xuecheng.learning.mq;


import com.xuecheng.framework.domain.task.XcTask;
import com.xuecheng.framework.exception.ExceptionCast;
import com.xuecheng.framework.model.response.CommonCode;
import com.xuecheng.learning.service.LearningService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class mqListner {

    public static final String XC_LEARNING_ADDCHOOSECOURSE = "xc_learning_addchoosecourse";
    public static final String EX_LEARNING_ADDCHOOSECOURSE = "ex_learning_addchoosecourse";
    public static final String XC_LEARNING_FINISHADDCHOOSECOURSE_KEY = "finishaddchoosecourse";
    @Autowired
    private LearningService learningService;

    @Autowired
    private RabbitTemplate rabbitTemplate;
    @RabbitListener(queues = XC_LEARNING_ADDCHOOSECOURSE )
    public void receiveChoosecourseTask(XcTask xcTask){
        Boolean aBoolean = learningService.addChooseCourse(xcTask);
        if (aBoolean){
            rabbitTemplate.convertAndSend(EX_LEARNING_ADDCHOOSECOURSE,XC_LEARNING_FINISHADDCHOOSECOURSE_KEY,xcTask);
            log.info("【学习中心】 选课信息添加成功!!");
        }
        else {
            ExceptionCast.cast(CommonCode.FAIL);
            log.error("【学习中心】 选课信息添加失败!!");
        }
    }
}
