package com.jbm.framework.masterdata.usage.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.MappedSuperclass;

/**
 * @program: JBM6
 * @author: wesley.zhang
 * @create: 2020-02-20 23:55
 **/
@Data
@TableName
@MappedSuperclass
@EqualsAndHashCode(callSuper = true)
public abstract class MasterDataIdEntity extends MasterDataEntity {


    public MasterDataIdEntity() {
        super();
    }

    public MasterDataIdEntity(Long id) {
        super();
        this.id = id;
    }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @TableId(type = IdType.ASSIGN_ID)
    @ApiModelProperty("主键ID")
    private Long id;


}
