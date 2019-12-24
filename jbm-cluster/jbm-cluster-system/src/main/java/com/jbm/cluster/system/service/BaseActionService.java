package com.jbm.cluster.system.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.jbm.cluster.api.model.entity.BaseAction;
import com.jbm.framework.masterdata.service.IMasterDataService;
import com.jbm.framework.usage.paging.PageForm;

import java.util.List;

/**
 * 操作资源管理
 *
 * @author liuyadu
 */
public interface BaseActionService extends IMasterDataService<BaseAction> {
    /**
     * 分页查询
     *
     * @param pageForm
     * @return
     */
    IPage<BaseAction> findListPage(PageForm pageForm);

    /**
     * 根据主键获取操作
     *
     * @param actionId
     * @return
     */
    BaseAction getAction(Long actionId);

    /**
     * 查询菜单下所有操作
     *
     * @param menuId
     * @return
     */
    List<BaseAction> findListByMenuId(Long menuId);

    /**
     * 检查操作编码是否存在
     *
     * @param actionCode
     * @return
     */
    Boolean isExist(String actionCode);


    /**
     * 添加操作资源
     *
     * @param action
     * @return
     */
    BaseAction addAction(BaseAction action);

    /**
     * 修改操作资源
     *
     * @param action
     * @return
     */
    BaseAction updateAction(BaseAction action);

    /**
     * 移除操作
     *
     * @param actionId
     * @return
     */
    void removeAction(Long actionId);

    /**
     * 移除菜单相关资源
     *
     * @param menuId
     */
    void removeByMenuId(Long menuId);
}
