package com.jbm.cluster.center.controller;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.jbm.cluster.api.model.IpLimitApi;
import com.jbm.cluster.api.model.RateLimitApi;
import com.jbm.cluster.api.model.entity.GatewayRoute;
import com.jbm.cluster.api.service.IGatewayServiceClient;
import com.jbm.cluster.center.service.GatewayIpLimitService;
import com.jbm.cluster.center.service.GatewayRateLimitService;
import com.jbm.cluster.center.service.GatewayRouteService;
import com.jbm.framework.metadata.bean.ResultBody;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

/**
 * 网关接口
 *
 * @author: wesley.zhang
 * @date: 2019/3/12 15:12
 * @description:
 */
@Api(tags = "网关对外接口")
@RestController
public class GatewayController implements IGatewayServiceClient {

    @Autowired
    private GatewayIpLimitService gatewayIpLimitService;
    @Autowired
    private GatewayRateLimitService gatewayRateLimitService;
    @Autowired
    private GatewayRouteService gatewayRouteService;

    @ApiOperation(value = "获取服务列表", notes = "获取服务列表")
    @GetMapping("/gateway/service/list")
    public ResultBody getServiceList() {
        List<Map> services = Lists.newArrayList();
        List<GatewayRoute> routes = gatewayRouteService.findRouteList();
        if (routes != null && routes.size() > 0) {
            routes.forEach(route -> {
                Map service = Maps.newHashMap();
                service.put("serviceId", route.getRouteName());
                service.put("serviceName", route.getRouteDesc());
                services.add(service);
            });
        }
        return ResultBody.ok().data(services);
    }

    /**
     * 获取接口黑名单列表
     *
     * @return
     */
    @ApiOperation(value = "获取接口黑名单列表", notes = "仅限内部调用")
    @GetMapping("/gateway/api/blackList")
    @Override
    public ResultBody<List<IpLimitApi>> getApiBlackList() {
        return ResultBody.ok().data(gatewayIpLimitService.findBlackList());
    }

    /**
     * 获取接口白名单列表
     *
     * @return
     */
    @ApiOperation(value = "获取接口白名单列表", notes = "仅限内部调用")
    @GetMapping("/gateway/api/whiteList")
    @Override
    public ResultBody<List<IpLimitApi>> getApiWhiteList() {
        return ResultBody.ok().data(gatewayIpLimitService.findWhiteList());
    }

    /**
     * 获取限流列表
     *
     * @return
     */
    @ApiOperation(value = "获取限流列表", notes = "仅限内部调用")
    @GetMapping("/gateway/api/rateLimit")
    @Override
    public ResultBody<List<RateLimitApi>> getApiRateLimitList() {
        return ResultBody.ok().data(gatewayRateLimitService.findRateLimitApiList());
    }

    /**
     * 获取路由列表
     *
     * @return
     */
    @ApiOperation(value = "获取路由列表", notes = "仅限内部调用")
    @GetMapping("/gateway/api/route")
    @Override
    public ResultBody<List<GatewayRoute>> getApiRouteList() {
        return ResultBody.ok().data(gatewayRouteService.findRouteList());
    }
}
