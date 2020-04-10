package com.xuecheng.learning.service;

import com.alibaba.fastjson.JSON;
import com.xuecheng.framework.domain.course.TeachplanMediaPub;
import com.xuecheng.framework.domain.learning.XcLearningCourse;
import com.xuecheng.framework.domain.learning.responses.GetMediaResult;
import com.xuecheng.framework.domain.learning.responses.LearningCode;
import com.xuecheng.framework.domain.task.XcTask;
import com.xuecheng.framework.domain.task.XcTaskHis;
import com.xuecheng.framework.exception.ExceptionCast;
import com.xuecheng.framework.model.response.CommonCode;
import com.xuecheng.learning.client.CourseSearchClient;
import com.xuecheng.learning.dao.XcLearningRepositry;
import com.xuecheng.learning.dao.XcTaskHisRepositry;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Map;

@Service
public class LearningService {

    @Autowired
    private CourseSearchClient searchClient;
    @Autowired
    private XcLearningRepositry xcLearningRepositry;
    @Autowired
    private XcTaskHisRepositry xcTaskHisRepositry;
    /**
     *
     * @param courseId 用来校验 用户是否有权限播放该视频
     * @param teachplanId  用来 获取 视频播放地址
     * @return
     */
    public GetMediaResult getmedia(String courseId, String teachplanId) {
        TeachplanMediaPub getmedia = searchClient.getmedia(teachplanId);
        if (getmedia==null|| StringUtils.isEmpty(getmedia.getMediaUrl()))
        {
            //获取视频播放地址出错
            ExceptionCast.cast(LearningCode.LEARNING_GETMEDIA_ERROR);
        }
        String mediaUrl = getmedia.getMediaUrl();
        return new GetMediaResult(CommonCode.SUCCESS,mediaUrl);
    }


    /**
     * 新增选课信息 和 将任务添加到 任务历史表
     */
    @Transactional
    public Boolean addChooseCourse(XcTask xcTask){
        //  1. 获取xcTask对象中的requestBody 转换成Map集合
        try {
            String requestBody = xcTask.getRequestBody();
            Map map = JSON.parseObject(requestBody, Map.class);
            String userId = map.get("userId").toString();
            String courseId = map.get("courseId").toString();
            String valid=null;
            if (map.get("valid")!=null){
                valid=map.get("valid").toString();
            }
            Date startTime = null;
            Date endTime = null;
            SimpleDateFormat dateFormat = new SimpleDateFormat("YYYY‐MM‐dd HH:mm:ss");
            // 如果有 startTime endTime 则将其转换成  YYYY‐MM‐dd HH:mm:ss
            if(map.get("startTime")!=null){
                startTime =dateFormat.parse((String) map.get("startTime"));
            }
            if(map.get("endTime")!=null){
                endTime =dateFormat.parse((String) map.get("endTime"));
            }
            //  查询 选课是否已经存在
            XcLearningCourse xcLearningCourse = new XcLearningCourse();
            XcLearningCourse learningCourse = xcLearningRepositry.findByCourseIdAndUserId(courseId, userId);
            if (learningCourse!=null){
                //存在 则将查询 出的内容 复制给xcLearningCourse 并延长时间
                BeanUtils.copyProperties(learningCourse,xcLearningCourse);
                xcLearningCourse.setStartTime(startTime);
                xcLearningCourse.setEndTime(endTime);
                xcLearningCourse.setStatus("501001");
                xcLearningCourse.setValid(valid);
            }
            else
            {
                xcLearningCourse.setCourseId(courseId);
                xcLearningCourse.setUserId(userId);
                xcLearningCourse.setValid(valid);
                xcLearningCourse.setStartTime(startTime);
                xcLearningCourse.setEndTime(endTime);
                xcLearningCourse.setStatus("501001");
            }
            // 3. 新增或更新选课信息
            xcLearningRepositry.save(xcLearningCourse);
            // 4. 向任务历史表中 添加记录
            XcTaskHis xcTaskHis = new XcTaskHis();
            BeanUtils.copyProperties(xcTask,xcTaskHis);
            xcTaskHis.setVersion(xcTask.getVersion().toString());
            xcTaskHisRepositry.save(xcTaskHis);
            return true;
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return false;
    }
}
