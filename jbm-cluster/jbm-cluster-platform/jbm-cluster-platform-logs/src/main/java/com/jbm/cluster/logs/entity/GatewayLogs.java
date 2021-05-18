package com.jbm.cluster.logs.entity;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.Document;

import java.io.Serializable;
import java.util.Date;

/**
 * @program: JBM6
 * @author: wesley.zhang
 * @create: 2021-05-06 16:52
 **/
@Data
@Document(indexName = "gateway_logs")
public class GatewayLogs implements Serializable {
    /**
     * 访问ID
     */
    @Id
    private String accessId;

    /**
     * 访问路径
     */
    private String path;

    /**
     * 请求类型
     */
    private String method;

    /**
     * 请求IP
     */
    private String ip;

    /**
     * 响应状态
     */
    private String httpStatus;

    /**
     * 请求时间
     */
    private Date requestTime;

    /**
     * 响应时间
     */
    private Date responseTime;

    /**
     * 耗时
     */
    private Long useTime;

    /**
     * 请求数据
     */
    private String params;

    /**
     * 请求头
     */
    private String headers;

    private String userAgent;

    /**
     *区域
     */
    private String region;

    /**
     * 认证用户信息
     */
    private String authentication;

    /**
     * 服务名
     */
    private String serviceId;

    /**
     * 错误信息
     */
    private String error;


}
