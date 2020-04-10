package com.xuecheng.manage_cms.controller;

import com.xuecheng.api.course.sysControllerApi;
import com.xuecheng.framework.domain.system.SysDictionary;

import com.xuecheng.manage_cms.service.sysService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/sys")
public class SysController  implements sysControllerApi {

    @Autowired
    private sysService sysService;

    @Override
    @GetMapping("/dictionary/get/{dType}")
    public SysDictionary getDictionary(@PathVariable String dType) {
        return sysService.getDictionary(dType);
    }
}
