package com.xuecheng.manage_course.service;

import com.alibaba.fastjson.JSON;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.xuecheng.framework.domain.cms.CmsPage;
import com.xuecheng.framework.domain.cms.response.CmsPageResult;
import com.xuecheng.framework.domain.cms.response.CmsPostPageResult;
import com.xuecheng.framework.domain.course.*;
import com.xuecheng.framework.domain.course.ext.CourseInfo;
import com.xuecheng.framework.domain.course.ext.CourseView;
import com.xuecheng.framework.domain.course.ext.TeachplanNode;
import com.xuecheng.framework.domain.course.request.CourseListRequest;
import com.xuecheng.framework.domain.course.response.CourseCode;
import com.xuecheng.framework.domain.course.response.CoursePublishResult;
import com.xuecheng.framework.exception.ExceptionCast;
import com.xuecheng.framework.model.response.CommonCode;
import com.xuecheng.framework.model.response.QueryResponseResult;
import com.xuecheng.framework.model.response.QueryResult;
import com.xuecheng.framework.model.response.ResponseResult;
import com.xuecheng.manage_course.Client.CmsPageClient;
import com.xuecheng.manage_course.dao.*;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.data.web.SpringDataWebProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;

@Service
public class CourseService {

    @Autowired
    private  TeachplanMediaRepositry teachplanMediaRepositry;
    @Autowired
    private  TeachplanMediaPubRepositry teachplanMediaPubRepositry;
    @Autowired
    private CoursePubRepositry coursePubRepositry;
    @Autowired
    private CmsPageClient cmsPageClient;
    @Autowired
    private  CoursePicRepositry coursePicRepositry;
    @Autowired
    private CourseMarketRepositry courseMarketRepositry;
    @Autowired
    private  CourseMapper courseMapper;
    @Autowired
    private TeachplanMapper teachplanMapper;
    @Autowired
    private TeachplanRepositry teachplanRepositry;
    @Autowired
    private CourseBaseRepository courseBaseRepository;
    @Value("${course‐publish.dataUrlPre}")
    private String publish_dataUrlPre;
    @Value("${course‐publish.pagePhysicalPath}")
    private String publish_page_physicalpath;
    @Value("${course‐publish.pageWebPath}")
    private String publish_page_webpath;
    @Value("${course‐publish.siteId}")
    private String publish_siteId;
    @Value("${course‐publish.templateId}")
    private String publish_templateId;
    @Value("${course‐publish.previewUrl}")
    private String previewUrl;
    /**
     * 查找课程计划节点
     * @param courseId
     * @return
     */
    public TeachplanNode selectList(String courseId){
        TeachplanNode teachplanNode = teachplanMapper.selectList(courseId);
        if (teachplanNode==null){
            ExceptionCast.cast(CommonCode.FAIL);
        }
        return teachplanNode;
    }

    public ResponseResult addTeachplan(Teachplan teachplan) {
        //判断参数是否为空
        if (teachplan==null|| StringUtils.isEmpty(teachplan.getCourseid())||StringUtils.isEmpty(teachplan.getPname()))
        {
            ExceptionCast.cast(CommonCode.INVALID_PARAM);
        }
        //获取课程id和parentid
        String courseid = teachplan.getCourseid();
        String parentid = teachplan.getParentid();
        //判断parentId是否存在
        if (StringUtils.isEmpty(parentid)){
             parentid = getTeachplanRoot(courseid);

        }
        Optional<Teachplan> optional = teachplanRepositry.findById(parentid);
        if (!optional.isPresent()){
            return  null;
        }
        Teachplan teachplanParent = optional.get();
        String grade = teachplanParent.getGrade();
        teachplan.setParentid(grade);
        teachplan.setCourseid(teachplanParent.getCourseid());
        teachplan.setStatus("0");
        if (grade.equals("1")) {
            teachplan.setGrade("2");
        }
        else if(grade.equals("2")) {
            teachplan.setGrade("3");
        }
        //将teachplan存入数据库
        teachplanRepositry.save(teachplan);
        return new ResponseResult(CommonCode.SUCCESS);
    }

    private String getTeachplanRoot( String courseid) {
        //不存在 则表示上级节点是根节点 需要判断根节点是否存在 如果不存在则自动创建
        List<Teachplan> teachplans = teachplanRepositry.findByCourseidAndParentid(courseid, "0");
        if (teachplans==null){
            //自己创建根节点
            Optional<CourseBase> optional = courseBaseRepository.findById(courseid);
            if (!optional.isPresent()){
                return null;
            }
            CourseBase courseBase = optional.get();
            Teachplan teachplanNew = new Teachplan();
            teachplanNew.setCourseid(courseid);
            teachplanNew.setPname(courseBase.getName());
            teachplanNew.setParentid("0");
            teachplanNew.setGrade("1");
            teachplanNew.setStatus("0");
            teachplanRepositry.save(teachplanNew);
            return teachplanNew.getId();
        }
        Teachplan teachplan1 = teachplans.get(0);
        return teachplan1.getId();
    }

    public QueryResponseResult findCourseList(int page, int size, CourseListRequest courseListRequest,String company_id) {
        PageHelper.startPage(page,size);
        if (courseListRequest==null){
            courseListRequest=new CourseListRequest();
        }
        courseListRequest.setCompanyId(company_id);
        Page<CourseInfo> courseList = courseMapper.findCourseListByCompanyId(courseListRequest);
        if (courseList.isEmpty())
        {
            ExceptionCast.cast(CommonCode.INVALID_PARAM);
        }
        List<CourseInfo> result = courseList.getResult();
        QueryResult<CourseInfo> queryResult=new QueryResult<>();
        queryResult.setList(result);
        queryResult.setTotal(courseList.getTotal());
        return new QueryResponseResult(CommonCode.SUCCESS,queryResult);
    }

    @Transactional
    public ResponseResult addCourseBase(CourseBase courseBase) {
        if (courseBase==null){
            ExceptionCast.cast(CommonCode.INVALID_PARAM);
        }
        courseBase.setStatus("202001");
        courseBaseRepository.save(courseBase);
        return new ResponseResult(CommonCode.SUCCESS);
    }

    public CourseBase findCourseBaseById(String courseId) {
        Optional<CourseBase> optional = courseBaseRepository.findById(courseId);
        if(!optional.isPresent()){
            ExceptionCast.cast(CommonCode.QUERY_FAIL);
        }
        CourseBase courseBase = optional.get();
        return courseBase;
    }

    @Transactional
    public ResponseResult updateCourseBase(String id,CourseBase courseBase) {
        //存在则修改 不存在则新增
        courseBaseRepository.save(courseBase);
        return  new ResponseResult(CommonCode.SUCCESS);
    }


    public CourseMarket getCourseMarketById(String id) {
        Optional<CourseMarket> optional = courseMarketRepositry.findById(id);
        if (!optional.isPresent()){
            ExceptionCast.cast(CommonCode.QUERY_FAIL);
        }
        return optional.get();
    }

    public ResponseResult updateCourseMarket(String id, CourseMarket courseMarket) {
        //存在则修改 不存在则新增
        courseMarketRepositry.save(courseMarket);
        return  new ResponseResult(CommonCode.SUCCESS);
    }

    public ResponseResult addPicWithCourse(String courseId, String pic) {
        Optional<CoursePic> optional = coursePicRepositry.findById(courseId);
        if (!optional.isPresent()){
            ExceptionCast.cast(CommonCode.INVALID_PARAM);
        }
        CoursePic coursePic = optional.get();
        coursePic.setPic(pic);
        coursePicRepositry.save(coursePic);
        return new ResponseResult(CommonCode.SUCCESS);
    }

    public CoursePic findCoursepic(String courseId) {
        if (StringUtils.isEmpty(courseId)){
            ExceptionCast.cast(CommonCode.INVALID_PARAM);
        }
        Optional<CoursePic> optional = coursePicRepositry.findById(courseId);
        if (!optional.isPresent()){
            return null;
        }
        return optional.get();
    }

    public ResponseResult deleteCoursepic(String courseId) {
        if (StringUtils.isEmpty(courseId)){
            ExceptionCast.cast(CommonCode.INVALID_PARAM);
        }
        coursePicRepositry.deleteById(courseId);
        return new ResponseResult(CommonCode.SUCCESS);
    }

    public CourseView courseview(String courseId) {
        //查询课程基本信息
        CourseBase courseBase = findCourseBaseById(courseId);
        //查询课程营销
        CourseMarket courseMarket = getCourseMarketById(courseId);
        //查询课程图片
        CoursePic coursepic = findCoursepic(courseId);
        //查询教学计划
        TeachplanNode teachplanNode = selectList(courseId);

        CourseView courseView = new CourseView();

        courseView.setCourseMarket(courseMarket);
        courseView.setCoursePic(coursepic);
        courseView.setCourseBase(courseBase);
        courseView.setTeachplanNode(teachplanNode);

        return courseView;
    }

    /**
     *
     * @param id  课程id
     * @return
     */
    public CoursePublishResult preview(String id) {
        //请求添加页面
        //准备页面信息
        CourseBase courseBase = findCourseBaseById(id);
        CmsPage cmsPage = new CmsPage();
        cmsPage.setSiteId(publish_siteId);
        cmsPage.setDataUrl(publish_dataUrlPre+id);
        cmsPage.setPageName(id+".html");
        cmsPage.setPageAliase(courseBase.getName());
        cmsPage.setPageWebPath(publish_page_webpath);
        cmsPage.setPagePhysicalPath(publish_page_physicalpath);
        cmsPage.setTemplateId(publish_templateId);
        //远程调用添加页面
        CmsPageResult cmsPageResult = cmsPageClient.save(cmsPage);
        if (!cmsPageResult.isSuccess()){
            return new CoursePublishResult(CommonCode.FAIL,null);
        }
        String pageId = cmsPageResult.getCmsPage().getPageId();
        //拼接url
        String Url=previewUrl+pageId;
        return  new CoursePublishResult(CommonCode.SUCCESS,Url);
    }

    //因为是发布页面 需要将数据保存到数据库  所以需要事务支持
    @Transactional
    public CoursePublishResult publish(String id) {
        //1. 准备cmsPage信息
        CourseBase courseBase = findCourseBaseById(id);
        if (courseBase==null){
            return new CoursePublishResult(CommonCode.FAIL,null);
        }
        CmsPage cmsPage = new CmsPage();
        cmsPage.setSiteId(publish_siteId);
        cmsPage.setDataUrl(publish_dataUrlPre+id);
        cmsPage.setPageName(id+".html");
        cmsPage.setPageAliase(courseBase.getName());
        cmsPage.setPageWebPath(publish_page_webpath);
        cmsPage.setPagePhysicalPath(publish_page_physicalpath);
        cmsPage.setTemplateId(publish_templateId);
        //2. 一键发布
        CmsPostPageResult cmsPostPageResult = cmsPageClient.postPageQuick(cmsPage);
        if (!cmsPostPageResult.isSuccess()){
            return  new CoursePublishResult(CommonCode.FAIL,null);
        }
        //3. 修改发布状态
        courseBase.setStatus("202002");
        courseBaseRepository.save(courseBase);

        //4. 通过课程id查询coursebase  coursepic coursemarket teachlan  合并到 coursepub中
        CoursePub coursePub = createCoursePub(id);
        //5. 将 coursepub 保存到数据库中
        CoursePub pub = saveCoursePub(id, coursePub);
        //6. 将 teachplan_media_pub 保存到数据库
        saveTeachplanMediaPub(id);
        return new CoursePublishResult(CommonCode.SUCCESS,cmsPostPageResult.getPageUrl());
    }

    private void saveTeachplanMediaPub(String id) {
        //删除courseId为指定值的记录
        teachplanMediaPubRepositry.deleteByCourseId(id);
        ///通过 id  查询
        List<TeachplanMedia> teachplanMediaList = teachplanMediaRepositry.findByCourseId(id);
        if (teachplanMediaList==null||teachplanMediaList.size()==0){
            ExceptionCast.cast(CourseCode.THIS_COURSE_HAS_NO_MEDIS);
        }

        List<TeachplanMediaPub> list=new ArrayList<>();
        for (TeachplanMedia teachplanMedia : teachplanMediaList) {
            TeachplanMediaPub teachplanMediaPub = new TeachplanMediaPub();
            BeanUtils.copyProperties(teachplanMedia,teachplanMediaPub);
            teachplanMediaPub.setTimestamp(new Date());
            list.add(teachplanMediaPub);
        }
        //将数据添加到数据库
        teachplanMediaPubRepositry.saveAll(list);
    }

    public CoursePub saveCoursePub(String id, CoursePub coursePub) {
        if(StringUtils.isEmpty(id)){
            ExceptionCast.cast(CourseCode.COURSE_PUBLISH_COURSEIDISNULL);
        }
        CoursePub coursePubNew = null;
        //判断数据库中是否存在课程对应的coursepub
        Optional<CoursePub> coursePubOptional = coursePubRepositry.findById(id);
        //如果存在 则修改coursepub
        if (coursePubOptional.isPresent()){
            coursePubNew=coursePubOptional.get();
        }else {
            coursePubNew= new CoursePub();
        }
        //将传入的coursePub的值复制给coursePubNew
        BeanUtils.copyProperties(coursePub,coursePubNew);
        coursePubNew.setId(id);
        coursePubNew.setTimestamp(new Date());
        SimpleDateFormat simpleDateFormat=new SimpleDateFormat("YYYY-MM-dd HH:mm:ss");
        coursePubNew.setPubTime(simpleDateFormat.format(new Date()));
        //将coursePubNew保存到数据库
        coursePubRepositry.save(coursePubNew);
        return coursePubNew;
    }


    /**
     * 创建 coursesPub
     * @param id   课程id
     * @return
     */
    public CoursePub  createCoursePub(String id){
        CoursePub coursePub = new CoursePub();
        coursePub.setId(id);
        //1. 获取 coursebase
        CourseBase courseBase = findCourseBaseById(id);
        //将coursebase中的属性的值 复制到 coursepub中
        BeanUtils.copyProperties(courseBase,coursePub);
        //2. 获取 coursemarket
        CourseMarket courseMarket = getCourseMarketById(id);
        BeanUtils.copyProperties(courseMarket,coursePub);
        //3. 获取 coursepic
        CoursePic coursepic = findCoursepic(id);
        BeanUtils.copyProperties(coursepic,coursePub);
        //4. 获取 teachplan
        TeachplanNode teachplanNode = selectList(id);
        String s = JSON.toJSONString(teachplanNode);
        coursePub.setTeachplan(s);
        return coursePub;
    }

    public ResponseResult savemedia(TeachplanMedia teachplanMedia) {
        //判断是否为空
        if (teachplanMedia==null){
            ExceptionCast.cast(CommonCode.INVALID_PARAM);
        }
        //查询 课程计划
        String teachplanId = teachplanMedia.getTeachplanId();
        Optional<Teachplan> optionalTeachplan = teachplanRepositry.findById(teachplanId);
        if (!optionalTeachplan.isPresent()){
            ExceptionCast.cast(CourseCode.TEACHPLAN_IS_NULL);
        }
        Teachplan teachplan = optionalTeachplan.get();
        //只有 grade为3 的课程计划 才可以 选择 媒资文件进行关联
        if (teachplan.getGrade()==null||!teachplan.getGrade().equals("3")){
            ExceptionCast.cast(CourseCode.COURSE_MEDIA_TEACHPLAN_GRADEERROR);
        }
        //查询 teachplanMedia是否存在  如果存在 更新 否则 插入
        Optional<TeachplanMedia> optionalTeachplanMedia = teachplanMediaRepositry.findById(teachplanId);
        TeachplanMedia teachplanMedia1=null;
        //不存在
        if (!optionalTeachplanMedia.isPresent())
        {
            teachplanMedia1=new TeachplanMedia();
        }
        else {
            teachplanMedia1=optionalTeachplanMedia.get();
        }
        teachplanMedia1.setTeachplanId(teachplanId);
        teachplanMedia1.setCourseId(teachplanMedia.getCourseId());
        teachplanMedia1.setMediaFileOriginalName(teachplanMedia.getMediaFileOriginalName());
        teachplanMedia1.setMediaId(teachplanMedia.getMediaId());
        teachplanMedia1.setMediaUrl(teachplanMedia.getMediaUrl());
        teachplanMediaRepositry.save(teachplanMedia1);
        return new ResponseResult(CommonCode.SUCCESS);
    }
}
