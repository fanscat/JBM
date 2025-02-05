package com.jbm.cluster.api.model.entitys.message;

import lombok.Data;

import java.util.Map;

/**
 * @author wesley.zhang
 * @date 2018-3-27
 **/
@Data
public class MqttNotification extends NotificationModel {

    /**
     * 通道
     */
    private String topic;

    /**
     * 消息体
     */
    private Object body;

    private Integer qos = 1;
}
