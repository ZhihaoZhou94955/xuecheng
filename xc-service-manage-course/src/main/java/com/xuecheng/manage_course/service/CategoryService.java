package com.xuecheng.manage_course.service;

import com.xuecheng.framework.domain.course.ext.CategoryNode;
import com.xuecheng.framework.exception.ExceptionCast;
import com.xuecheng.framework.model.response.CommonCode;
import com.xuecheng.manage_course.dao.CategoryNodeMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CategoryService {

    @Autowired
    private CategoryNodeMapper categoryNodeMapper;


    public CategoryNode findCategoryList() {
        CategoryNode categoryList = categoryNodeMapper.findCategoryList();
        if (categoryList==null){
            ExceptionCast.cast(CommonCode.FAIL);
        }
        return  categoryList;

    }
}
