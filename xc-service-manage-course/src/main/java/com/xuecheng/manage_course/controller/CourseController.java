package com.xuecheng.manage_course.controller;

import com.xuecheng.api.course.CourseControllerApi;
import com.xuecheng.framework.domain.course.*;
import com.xuecheng.framework.domain.course.ext.CourseView;
import com.xuecheng.framework.domain.course.ext.TeachplanNode;
import com.xuecheng.framework.domain.course.request.CourseListRequest;
import com.xuecheng.framework.domain.course.response.CoursePublishResult;
import com.xuecheng.framework.exception.ExceptionCast;
import com.xuecheng.framework.model.response.CommonCode;
import com.xuecheng.framework.model.response.QueryResponseResult;
import com.xuecheng.framework.model.response.ResponseResult;
import com.xuecheng.framework.utils.Oauth2Util;
import com.xuecheng.framework.utils.XcOauth2Util;
import com.xuecheng.framework.web.BaseController;
import com.xuecheng.manage_course.dao.CourseMarketRepositry;
import com.xuecheng.manage_course.service.CourseService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/course")
public class CourseController extends BaseController implements CourseControllerApi {


    @Autowired
    private CourseService courseService;

    /**
     * 查询课程计划节点
     * @param courseId
     * @return
     */
    @GetMapping("/teachplan/list/{courseId}")
    public TeachplanNode selectList(@PathVariable String courseId){
          return   courseService.selectList(courseId);
    }

    /**
     * 新增课程计划
     * @param teachplan
     * @return
     */
    @Override
    @PostMapping("/teachplan/add")
    public ResponseResult addTeachplan(@RequestBody Teachplan teachplan) {
        return courseService.addTeachplan(teachplan);
    }

    /**
     * 分页查询课程
     * @param page
     * @param size
     * @param courseListRequest
     * @return
     */
    @Override
    @GetMapping("/coursebase/list/{page}/{size}")
    public QueryResponseResult findCourseList(@PathVariable("page") int page,@PathVariable("size") int size,
                                              CourseListRequest courseListRequest) {
//        String company_id="1";
        XcOauth2Util xcOauth2Util=new XcOauth2Util();
        XcOauth2Util.UserJwt userJwt = xcOauth2Util.getUserJwtFromHeader(request);
        String companyId = userJwt.getCompanyId();
        return courseService.findCourseList(page,size,courseListRequest,companyId);
    }

    /**
     * 新增课程
     * @param courseBase
     * @return
     */
    @Override
    @PostMapping("/coursebase/add")
    public ResponseResult addCourseBase(@RequestBody  CourseBase courseBase) {
        return courseService.addCourseBase(courseBase);
    }

    /**
     * 通过Id查询课程
     * @param courseId
     * @return
     */
    @Override
    @GetMapping("/coursebase/{courseId}")
    @PreAuthorize("hasAuthority('course_get_baseinfo')")
    public CourseBase findCourseBaseById(@PathVariable String courseId) {
        return courseService.findCourseBaseById(courseId);
    }

    /**
     * 更新课程基本信息
     * @param id
     * @param courseBase
     * @return
     */
    @Override
    @PostMapping("/coursebase/update/{id}")
    public ResponseResult updateCourseBase(@PathVariable String id,@RequestBody CourseBase courseBase) {
        return courseService.updateCourseBase(id,courseBase);
    }

    /**
     * 通过Id查询课程营销信息
     * @param id
     * @return
     */
    @Override
    @GetMapping("/coursemark/{id}")
    public CourseMarket getCourseMarketById(@PathVariable String id) {
        return courseService.getCourseMarketById(id);
    }

    /**
     * 修改课程营销信息
     * @param id
     * @param courseMarket
     * @return
     */
    @Override
    @PostMapping("/coursemark/update/{id}")
    public ResponseResult updateCourseMarket(@PathVariable String id, @RequestBody CourseMarket courseMarket) {
        return courseService.updateCourseMarket(id,courseMarket);
    }

    @Override
    @PostMapping("/coursepic/add")
    public ResponseResult addPicWithCourse(@RequestParam("courseId") String courseId,@RequestParam("pic") String pic) {
        return courseService.addPicWithCourse(courseId,pic);
    }

    @Override
    @GetMapping("/coursepic/list/{courseId}")
    @PreAuthorize("hasAuthority('course_find_pic')") //我们使用@PreAuthorize 来指定某些方法需要权限才能访问
    public CoursePic findCoursepic(@PathVariable String courseId) {
        return courseService.findCoursepic(courseId);
    }

    @Override
    @DeleteMapping("/coursepic/delete")
    public ResponseResult deleteCoursepic(@RequestParam("courseId") String courseId) {
        return courseService.deleteCoursepic(courseId);
    }

    @Override
    @GetMapping("/courseview/{id}")
    public CourseView courseview(@PathVariable("id") String courseId) {
        return courseService.courseview(courseId);
    }

    @Override
    @PostMapping("/preview/{id}")
    public CoursePublishResult preview(@PathVariable String id) {
        return courseService.preview(id);
    }

    @Override
    @PostMapping("/publish/{id}")
    public CoursePublishResult publish(@PathVariable String id) {
        return courseService.publish(id);
    }

    @Override
    @PostMapping("/savemedia")
    public ResponseResult savemedia(@RequestBody TeachplanMedia teachplanMedia) {
        return courseService.savemedia(teachplanMedia);
    }


}
