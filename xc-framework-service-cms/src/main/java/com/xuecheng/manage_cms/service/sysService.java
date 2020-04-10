package com.xuecheng.manage_cms.service;

import com.xuecheng.framework.domain.course.response.CourseCode;
import com.xuecheng.framework.domain.system.SysDictionary;
import com.xuecheng.framework.exception.ExceptionCast;

import com.xuecheng.manage_cms.dao.sysRepositry;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class sysService {

    @Autowired
    private sysRepositry sysRepositry;

    public SysDictionary getDictionary(String dType) {
        SysDictionary sysDictionary = sysRepositry.findByDType(dType);
        if (sysDictionary==null){
            ExceptionCast.cast(CourseCode.SYS_DICTIONARY_QUERY_FAIL);
        }
        return  sysDictionary;
    }
}
