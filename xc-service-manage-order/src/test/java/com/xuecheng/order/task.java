package com.xuecheng.order;

import com.xuecheng.framework.domain.task.XcTask;
import com.xuecheng.order.dao.XcTaskRepositry;
import com.xuecheng.order.service.TaskService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.transaction.annotation.Transactional;

import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;

@SpringBootTest
@RunWith(SpringRunner.class)
public class task {

    @Test
    public  void test(){
//        Calendar calendar =new GregorianCalendar();
//        calendar.setTime(new Date());
//        calendar.add(GregorianCalendar.MINUTE,-1);
//        Date time = calendar.getTime();
//        System.out.println(calendar);
//        System.out.println(time);
        Calendar instance = Calendar.getInstance();
        instance.add(Calendar.MINUTE,-1);
        Date time = instance.getTime();
        System.out.println(time);
    }
    @Autowired
    private TaskService taskService;
    @Autowired
    private XcTaskRepositry xcTaskRepositry;
    @Test
    @Transactional
    @Rollback(false)
    public  void test2(){
       /* Calendar instance = Calendar.getInstance();
        instance.add(Calendar.MINUTE,-1);
        Date time = instance.getTime();
        List<XcTask> taskList = taskService.findTaskList(10, time);
        for (XcTask xcTask : taskList) {
            System.out.println(xcTask);
        }*/
      xcTaskRepositry.updateTaskTime("10",new Date());
    }
}
