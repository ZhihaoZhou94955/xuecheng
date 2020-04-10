package com.xuecheng.framework.domain.cms.request;

import com.xuecheng.framework.model.request.RequestData;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;


@Data
@ApiModel(value = "QueryPageRequest",description = "分页查询的条件")
public class QueryPageRequest  extends RequestData {
    //页面Id
    @ApiModelProperty(value = "页面Id")
    private String pageId;
    //站点Id
    @ApiModelProperty(value = "站点Id")
    private String siteId;
    //模板Id
    @ApiModelProperty(value = "模板Id")
    private String templateId;
    //页面名称
    @ApiModelProperty(value = "页面名称")
    private String pageName;
    //页面别名
    @ApiModelProperty(value = "页面别名")
    private String pageAliase;
}
