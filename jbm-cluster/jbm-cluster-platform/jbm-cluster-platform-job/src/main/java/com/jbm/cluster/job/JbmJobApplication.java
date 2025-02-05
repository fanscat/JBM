package com.jbm.cluster.job;

import com.jbm.autoconfig.dic.annotation.EnableJbmDictionary;
import com.jbm.cluster.api.constants.job.MisfirePolicy;
import com.jbm.cluster.api.model.entitys.job.SysJob;
import com.jbm.cluster.node.configuration.annotation.EnableJbmCluster;
import com.jbm.cluster.job.controller.SysJobController;
import com.jbm.cluster.job.mapper.SysJobMapper;
import com.jbm.framework.masterdata.code.EnableCodeAutoGeneate;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;

/**
 * 定时任务
 *
 * @author wesley
 */
@EnableJbmCluster(targetPackageClasses = SysJobController.class)
@EnableCaching
@EnableFeignClients
@EnableDiscoveryClient
@SpringBootApplication
@EntityScan(basePackages = {"com.jbm.cluster.api.model.entitys.job"})
@MapperScan(basePackageClasses = SysJobMapper.class)
@EnableJbmDictionary(basePackageClasses = MisfirePolicy.class)
@EnableCodeAutoGeneate(entityPackageClasses = {SysJob.class}, targetPackage = "com.jbm.cluster.job")
public class JbmJobApplication {
    public static void main(String[] args) {
        SpringApplication.run(JbmJobApplication.class, args);
    }
}
