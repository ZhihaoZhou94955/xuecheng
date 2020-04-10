package com.xuecheng.learning.dao;

import com.xuecheng.framework.domain.learning.XcLearningCourse;
import org.springframework.data.jpa.repository.JpaRepository;

public interface XcLearningRepositry extends JpaRepository<XcLearningCourse,String> {

    public XcLearningCourse findByCourseIdAndUserId(String courseId,String UserId);
}
