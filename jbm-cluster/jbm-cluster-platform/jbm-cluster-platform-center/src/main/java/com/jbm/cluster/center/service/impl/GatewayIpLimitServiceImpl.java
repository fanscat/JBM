package com.jbm.cluster.center.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.ObjectUtils;
import com.jbm.cluster.api.model.IpLimitApi;
import com.jbm.cluster.api.entitys.gateway.GatewayIpLimit;
import com.jbm.cluster.api.entitys.gateway.GatewayIpLimitApi;
import com.jbm.cluster.center.mapper.GatewayIpLimitApiMapper;
import com.jbm.cluster.center.mapper.GatewayIpLimitMapper;
import com.jbm.cluster.center.service.GatewayIpLimitService;
import com.jbm.framework.masterdata.usage.form.PageRequestBody;
import com.jbm.framework.service.mybatis.MasterDataServiceImpl;
import com.jbm.framework.usage.paging.DataPaging;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;

/**
 * @author wesley.zhang
 */
@Slf4j
@Service
@Transactional(rollbackFor = Exception.class)
public class GatewayIpLimitServiceImpl extends MasterDataServiceImpl<GatewayIpLimit> implements GatewayIpLimitService {

    @Autowired
    private GatewayIpLimitMapper gatewayIpLimitMapper;
    @Autowired
    private GatewayIpLimitApiMapper gatewayIpLimitApisMapper;


    /**
     * 分页查询
     *
     * @param pageRequestBody
     * @return
     */
    @Override
    public DataPaging<GatewayIpLimit> findListPage(PageRequestBody pageRequestBody) {
        GatewayIpLimit query = pageRequestBody.tryGet(GatewayIpLimit.class);
        QueryWrapper<GatewayIpLimit> queryWrapper = new QueryWrapper();
        queryWrapper.lambda()
                .likeRight(ObjectUtils.isNotEmpty(query.getPolicyName()), GatewayIpLimit::getPolicyName, query.getPolicyName())
                .eq(ObjectUtils.isNotEmpty(query.getPolicyType()), GatewayIpLimit::getPolicyType, query.getPolicyType());
        queryWrapper.orderByDesc("create_time");
        return this.selectEntitys(pageRequestBody.getPageParams(), queryWrapper);
    }

    /**
     * 查询白名单
     *
     * @return
     */
    @Override
    public List<IpLimitApi> findBlackList() {
        List<IpLimitApi> list = gatewayIpLimitApisMapper.selectIpLimitApi(0);
        return list;
    }

    /**
     * 查询黑名单
     *
     * @return
     */
    @Override
    public List<IpLimitApi> findWhiteList() {
        List<IpLimitApi> list = gatewayIpLimitApisMapper.selectIpLimitApi(1);
        return list;
    }

    /**
     * 查询策略已绑定API列表
     *
     * @return
     */
    @Override
    public List<GatewayIpLimitApi> findIpLimitApiList(Long policyId) {
        QueryWrapper<GatewayIpLimitApi> queryWrapper = new QueryWrapper();
        queryWrapper.lambda()
                .eq(GatewayIpLimitApi::getPolicyId, policyId);
        List<GatewayIpLimitApi> list = gatewayIpLimitApisMapper.selectList(queryWrapper);
        return list;
    }

    /**
     * 获取IP限制策略
     *
     * @param policyId
     * @return
     */
    @Override
    public GatewayIpLimit getIpLimitPolicy(Long policyId) {
        return gatewayIpLimitMapper.selectById(policyId);
    }

    /**
     * 添加IP限制策略
     *
     * @param policy
     */
    @Override
    public GatewayIpLimit addIpLimitPolicy(GatewayIpLimit policy) {
        policy.setCreateTime(new Date());
        policy.setUpdateTime(policy.getCreateTime());
        gatewayIpLimitMapper.insert(policy);
        return policy;
    }

    /**
     * 更新IP限制策略
     *
     * @param policy
     */
    @Override
    public GatewayIpLimit updateIpLimitPolicy(GatewayIpLimit policy) {
        policy.setUpdateTime(new Date());
        gatewayIpLimitMapper.updateById(policy);
        return policy;
    }

    /**
     * 删除IP限制策略
     *
     * @param policyId
     */
    @Override
    public void removeIpLimitPolicy(Long policyId) {
        clearIpLimitApisByPolicyId(policyId);
        gatewayIpLimitMapper.deleteById(policyId);
    }

    /**
     * 绑定API, 一个API只能绑定一个策略
     *
     * @param policyId
     * @param apis
     */
    @Override
    public void addIpLimitApis(Long policyId, String... apis) {
        // 先清空策略已有绑定
        clearIpLimitApisByPolicyId(policyId);
        if (apis != null && apis.length > 0) {
            for (String api : apis) {
                // 先api解除所有绑定, 一个API只能绑定一个策略
                Long apiId = Long.parseLong(api);
                clearIpLimitApisByApiId(apiId);
                GatewayIpLimitApi item = new GatewayIpLimitApi();
                item.setApiId(apiId);
                item.setPolicyId(policyId);
                // 重新绑定策略
                gatewayIpLimitApisMapper.insert(item);

            }
        }
    }

    /**
     * 清空绑定的API
     *
     * @param policyId
     */
    @Override
    public void clearIpLimitApisByPolicyId(Long policyId) {
        QueryWrapper<GatewayIpLimitApi> queryWrapper = new QueryWrapper();
        queryWrapper.lambda()
                .eq(GatewayIpLimitApi::getPolicyId, policyId);
        gatewayIpLimitApisMapper.delete(queryWrapper);
    }

    /**
     * API解除所有策略
     *
     * @param apiId
     */
    @Override
    public void clearIpLimitApisByApiId(Long apiId) {
        QueryWrapper<GatewayIpLimitApi> queryWrapper = new QueryWrapper();
        queryWrapper.lambda()
                .eq(GatewayIpLimitApi::getApiId, apiId);
        gatewayIpLimitApisMapper.delete(queryWrapper);
    }
}
