package com.xuecheng.manage_course.dao;

import com.xuecheng.framework.domain.course.TeachplanMediaPub;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface TeachplanMediaPubRepositry extends JpaRepository<TeachplanMediaPub,String> {

    public List<TeachplanMediaPub> deleteByCourseId(String courseId);

}
