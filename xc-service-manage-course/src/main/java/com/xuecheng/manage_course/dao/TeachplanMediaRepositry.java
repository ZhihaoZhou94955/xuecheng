package com.xuecheng.manage_course.dao;

import com.xuecheng.framework.domain.course.TeachplanMedia;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface TeachplanMediaRepositry extends JpaRepository<TeachplanMedia,String> {

    public List<TeachplanMedia> findByCourseId(String id);
}
