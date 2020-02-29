package com.jbm.cluster.api.model.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.jbm.cluster.api.model.AuthorityExt;
import com.jbm.framework.masterdata.usage.entity.MasterDataEntity;
import com.jbm.framework.masterdata.usage.entity.MasterDataIdEntity;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import javax.persistence.Entity;
import javax.persistence.Id;
import java.util.Date;

/**
 * 系统权限-应用关联
 *
 * @author wesley.zhang
 */
@Data
@Entity
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
@ApiModel("APP权限设置")
@TableName("base_authority_app")
public class BaseAuthorityApp extends AuthorityExt {

    /**
     * 应用ID
     */
    @ApiModelProperty("APPID")
    private String appId;

}
