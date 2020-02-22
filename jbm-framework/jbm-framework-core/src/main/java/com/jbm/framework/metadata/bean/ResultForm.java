package com.jbm.framework.metadata.bean;

import java.io.Serializable;
import java.text.MessageFormat;
import java.util.*;

import cn.hutool.core.util.ObjectUtil;
import com.alibaba.fastjson.annotation.JSONField;
import com.google.common.collect.Maps;
import com.jbm.framework.exceptions.ServiceException;
import com.jbm.framework.metadata.enumerate.ErrorCode;
import com.jbm.framework.metadata.enumerate.MessageEnum;
import com.jbm.framework.metadata.enumerate.ResultEnum;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * 后台返回给前台的封装类
 *
 * @author Wesley
 */
@ApiModel(value = "响应结果")
@Data
public class ResultForm<T> implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 响应编码
     */
    @ApiModelProperty(value = "响应编码:0-请求处理成功")
    private int code = 0;
    /**
     * 提示消息
     */
    @ApiModelProperty(value = "提示消息")
    private String message;

    /**
     * 请求路径
     */
    @ApiModelProperty(value = "请求路径")
    private String path;

    /**
     * 响应数据
     */
    @ApiModelProperty(value = "响应数据")
    private T result;

    /**
     * http状态码
     */
    private int httpStatus;

    /**
     * 附加数据
     */
    @ApiModelProperty(value = "附加数据")
    private Map<String, Object> extra;

    /**
     * 响应时间
     */
    @ApiModelProperty(value = "响应时间")
    private Date timestamp = new Date();

    public ResultForm() {
        super();
    }

    public static <T> ResultForm<T> success(T data, String msg) {
        return ResultForm.ok().result(data).msg(msg);
    }

    public static <T> ResultForm<T> error(T data, String msg) {
        return ResultForm.failed().result(data).msg(msg);
    }

    public static <T> ResultForm<T> error(Exception e) {
        return ResultForm.failed().msg(e.getMessage());
    }

    public static <T> ResultForm<T> error(T data, String msg, Exception e) {
        return ResultForm.failed().result(data).msg(msg);
    }


    public int getCode() {
        return code;
    }

    public String getMessage() {
        return message;
    }

    public String getPath() {
        return path;
    }


    public Map<String, Object> getExtra() {
        return extra;
    }

    public Date getTimestamp() {
        return timestamp;
    }

    @JSONField(serialize = false, deserialize = false)
    public int getHttpStatus() {
        return httpStatus;
    }

    @JSONField(serialize = false, deserialize = false)
    public boolean isOk() {
        return this.code == ErrorCode.OK.getCode();
    }


    public static ResultForm ok() {
        return new ResultForm().code(ErrorCode.OK.getCode()).msg(ErrorCode.OK.getMessage());
    }

    public static ResultForm failed() {
        return new ResultForm().code(ErrorCode.FAIL.getCode()).msg(ErrorCode.FAIL.getMessage());
    }

    public ResultForm code(int code) {
        this.code = code;
        return this;
    }

    public ResultForm msg(String message) {
        this.message = i18n(ErrorCode.getResultEnum(this.code).getMessage(), message);
        return this;
    }

    public ResultForm result(T result) {
        this.result = result;
        return this;
    }

    public ResultForm path(String path) {
        this.path = path;
        return this;
    }

    public ResultForm httpStatus(int httpStatus) {
        this.httpStatus = httpStatus;
        return this;
    }

    public ResultForm put(String key, Object value) {
        if (this.extra == null) {
            this.extra = Maps.newHashMap();
        }
        this.extra.put(key, value);
        return this;
    }

    @Override
    public String toString() {
        return "ResultForm{" +
                "code=" + code +
                ", message='" + message + '\'' +
                ", path='" + path + '\'' +
                ", result=" + result +
                ", httpStatus=" + httpStatus +
                ", extra=" + extra +
                ", timestamp=" + timestamp +
                '}';
    }

    /**
     * 错误信息配置
     */
    @JSONField(serialize = false, deserialize = false)
    private static ResourceBundle resourceBundle = ResourceBundle.getBundle("error");

    /**
     * 提示信息国际化
     *
     * @param message
     * @param defaultMessage
     * @return
     */
    @JSONField(serialize = false, deserialize = false)
    private static String i18n(String message, String defaultMessage) {
        return resourceBundle.containsKey(message) ? resourceBundle.getString(message) : defaultMessage;
    }


}
