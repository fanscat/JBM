package com.jbm.cluster.center.controller;

import com.jbm.cluster.api.model.entity.BaseReleaseInfo;
import com.jbm.cluster.center.service.BaseReleaseInfoService;
import com.jbm.framework.masterdata.usage.form.PageRequestBody;
import com.jbm.framework.metadata.bean.ResultBody;
import com.jbm.framework.mvc.web.MasterDataCollection;
import com.jbm.framework.usage.paging.DataPaging;
import com.jbm.framework.usage.paging.PageForm;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @Author: auto generate by jbm
 * @Create: 2021-08-25 10:49:30
 */
@Api(tags = "版本发布管理")
@RestController
@RequestMapping("/baseReleaseInfo")
public class BaseReleaseInfoController extends MasterDataCollection<BaseReleaseInfo, BaseReleaseInfoService> {

    @ApiOperation(value = "查询最后一个版本信息", notes = "查询最后一个版本信息")
    @PostMapping("/findLastVersionInfo")
    public ResultBody<BaseReleaseInfo> findLastVersionInfo(@RequestBody(required = false) BaseReleaseInfo releaseInfo) {
        try {
            BaseReleaseInfo reslut = service.findLastVersionInfo(releaseInfo);
            return ResultBody.success(reslut, "查询最后一个版本成功");
        } catch (Exception e) {
            return ResultBody.error(e);
        }
    }
}
