package com.xuecheng.ucenter.service;

import com.xuecheng.framework.domain.ucenter.XcCompanyUser;
import com.xuecheng.framework.domain.ucenter.XcMenu;
import com.xuecheng.framework.domain.ucenter.XcUser;
import com.xuecheng.framework.domain.ucenter.ext.XcUserExt;
import com.xuecheng.framework.domain.ucenter.response.UcenterCode;
import com.xuecheng.framework.exception.ExceptionCast;
import com.xuecheng.framework.model.response.CommonCode;
import com.xuecheng.ucenter.dao.xcCompanyuserRepositry;
import com.xuecheng.ucenter.dao.xcMenuMapper;
import com.xuecheng.ucenter.dao.xcUserRepositry;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UcenterService {

    @Autowired
    private xcUserRepositry xcUserRepositry;
    @Autowired
    private xcCompanyuserRepositry xcCompanyuserRepositry;
    @Autowired
    private xcMenuMapper xcMenuMapper;
    public XcUserExt getUserext(String username) {
        //1.查询用户基本信息
        XcUser xcUser = xcUserRepositry.findByUsername(username);
        if (xcUser==null){
            return null;
        }
        String id = xcUser.getId();
        //2. 通过 id 查询 公司信息
        XcCompanyUser xcCompanyUser = xcCompanyuserRepositry.findByUserId(id);
        //3. 查询用户权限
        List<XcMenu> menus = xcMenuMapper.findById(id);
        //因为有些用户是学生 学生是没有所属公司的  所以xcCompanyUser 是可以为空的 不需要抛出异常
        String companyId = xcCompanyUser.getCompanyId();
        XcUserExt xcUserExt=new XcUserExt();
        BeanUtils.copyProperties(xcUser,xcUserExt);
        xcUserExt.setCompanyId(companyId);
        xcUserExt.setPermissions(menus);
        return xcUserExt;
    }
}
