package com.jbm.cluster.center.mapper;

import com.jbm.cluster.api.model.IpLimitApi;
import com.jbm.cluster.api.model.entity.GatewayIpLimitApi;
import com.jbm.framework.masterdata.annotation.MapperRepository;
import com.jbm.framework.masterdata.mapper.SuperMapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * @Author: auto generate by jbm
 * @Create: 2020-02-25 03:47:52
 */
@MapperRepository
public interface GatewayIpLimitApiMapper extends SuperMapper<GatewayIpLimitApi> {
    List<IpLimitApi> selectIpLimitApi(@Param("policyType") int policyType);
}
