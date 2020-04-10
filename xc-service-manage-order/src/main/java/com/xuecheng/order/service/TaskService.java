package com.xuecheng.order.service;

import com.xuecheng.framework.domain.task.XcTask;
import com.xuecheng.framework.domain.task.XcTaskHis;
import com.xuecheng.order.dao.XcTaskHisRepositry;
import com.xuecheng.order.dao.XcTaskRepositry;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;
import java.util.Optional;

@Service
public class TaskService {
    @Autowired
    private XcTaskRepositry xcTaskRepositry;
    @Autowired
    private XcTaskHisRepositry xcTaskHisRepositry;
    public List<XcTask> findTaskList(int size,Date updateTime){
        PageRequest request = PageRequest.of(0, 10);
        //要传入一分钟之前的时间 只需获取当前时间 减 一分钟
        Page<XcTask> xcTasks = xcTaskRepositry.findByUpdateTimeBefore(request, updateTime);
        return xcTasks.getContent();
    }

    @Transactional
    public void updateTaskTime(String id,Date date){
        Optional<XcTask> optionalXcTask = xcTaskRepositry.findById(id);
        if (optionalXcTask.isPresent()){
            xcTaskRepositry.updateTaskTime(id,date);
        }
    }

    @Transactional
    public int updateVersion(String id,int version){
        int i = xcTaskRepositry.updateVersion(id, version);
        return i;
    }

    @Transactional
    public void finishTask(XcTask xcTask){
        String id = xcTask.getId();
        xcTaskRepositry.deleteById(id);
        XcTaskHis xcTaskHis = new XcTaskHis();
        BeanUtils.copyProperties(xcTask,xcTaskHis);
        xcTaskHis.setVersion(xcTask.getVersion().toString());
        xcTaskHisRepositry.save(xcTaskHis);
    }
}
