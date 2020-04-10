package com.xuecheng.api.course;

import com.xuecheng.framework.domain.course.*;
import com.xuecheng.framework.domain.course.ext.CourseView;
import com.xuecheng.framework.domain.course.ext.TeachplanNode;
import com.xuecheng.framework.domain.course.request.CourseListRequest;
import com.xuecheng.framework.domain.course.response.CoursePublishResult;
import com.xuecheng.framework.model.response.QueryResponseResult;
import com.xuecheng.framework.model.response.ResponseResult;
import org.springframework.web.bind.annotation.PathVariable;

public interface CourseControllerApi {
    public TeachplanNode selectList( String courseId);

    public ResponseResult addTeachplan(Teachplan teachplan);

    public QueryResponseResult findCourseList(int page, int size, CourseListRequest courseListRequest);

    public ResponseResult addCourseBase(CourseBase courseBase);

    public CourseBase  findCourseBaseById(String courseId);

    public ResponseResult updateCourseBase(String id,CourseBase courseBase);

    public CourseMarket getCourseMarketById(String id);

    public ResponseResult updateCourseMarket(String id,CourseMarket courseMarket);

    public ResponseResult addPicWithCourse(String courseId,String pic);

    public CoursePic findCoursepic(String courseId);

    public ResponseResult deleteCoursepic(String courseId);

    public CourseView courseview(String courseId);

    public CoursePublishResult preview(String id);

    public CoursePublishResult publish(String id);

    public ResponseResult savemedia(TeachplanMedia teachplanMedia);

}
