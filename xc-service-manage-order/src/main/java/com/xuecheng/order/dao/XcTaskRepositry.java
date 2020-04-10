package com.xuecheng.order.dao;

import com.xuecheng.framework.domain.task.XcTask;
import org.apache.ibatis.annotations.Param;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import java.util.Date;


public interface XcTaskRepositry extends JpaRepository<XcTask,String> {

    // 查询一分钟之前的 前N条记录
    public Page<XcTask> findByUpdateTimeBefore(Pageable pageable, Date updateTime);

    //更新 updatetime字段 使用 JPQL
    @Modifying
    @Query("update XcTask  set updateTime= :updatetime where id=:id")
    public int  updateTaskTime(@Param("id") String id,@Param("updatetime") Date updatetime);

    @Modifying
    @Query("update XcTask  set version=:version+1 where version=:version and id=:id")
    public int  updateVersion(@Param("id") String id,@Param("version") int version);
}
