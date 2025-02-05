package com.jbm.cluster.center.service;

import com.jbm.cluster.api.model.entity.BaseReleaseInfo;
import com.jbm.framework.masterdata.service.IMasterDataService;

/**
 * @Author: auto generate by jbm
 * @Create: 2021-08-25 10:49:30
 */
public interface BaseReleaseInfoService extends IMasterDataService<BaseReleaseInfo> {

    BaseReleaseInfo findLastVersionInfo(BaseReleaseInfo releaseInfo);
}
