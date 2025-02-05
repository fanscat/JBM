package com.jbm.cluster.center.service.impl;

import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.ObjectUtils;
import com.jbm.cluster.api.constants.BaseConstants;
import com.jbm.cluster.api.constants.ResourceType;
import com.jbm.cluster.api.model.entity.BaseMenu;
import com.jbm.cluster.center.mapper.BaseMenuMapper;
import com.jbm.cluster.center.service.BaseActionService;
import com.jbm.cluster.center.service.BaseAuthorityService;
import com.jbm.cluster.center.service.BaseMenuService;
import com.jbm.cluster.common.exception.OpenAlertException;
import com.jbm.framework.masterdata.usage.form.PageRequestBody;
import com.jbm.framework.service.mybatis.MasterDataServiceImpl;
import com.jbm.framework.usage.paging.DataPaging;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
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
public class BaseMenuServiceImpl extends MasterDataServiceImpl<BaseMenu> implements BaseMenuService {
    @Autowired
    private BaseMenuMapper baseMenuMapper;
    @Autowired
    private BaseAuthorityService baseAuthorityService;

    @Autowired
    private BaseActionService baseActionService;

    @Value("${spring.application.name}")
    private String DEFAULT_SERVICE_ID;

    /**
     * 分页查询
     *
     * @param pageRequestBody
     * @return
     */
    @Override
    public DataPaging<BaseMenu> findListPage(PageRequestBody pageRequestBody) {
        BaseMenu query = pageRequestBody.tryGet(BaseMenu.class);
        QueryWrapper<BaseMenu> queryWrapper = new QueryWrapper();
        queryWrapper.lambda()
                .likeRight(ObjectUtils.isNotEmpty(query.getMenuCode()), BaseMenu::getMenuCode, query.getMenuCode())
                .likeRight(ObjectUtils.isNotEmpty(query.getMenuName()), BaseMenu::getMenuName, query.getMenuName());
        return this.selectEntitys(pageRequestBody.getPageParams(), queryWrapper);
    }

    /**
     * 查询列表
     *
     * @return
     */
    @Override
    public List<BaseMenu> findAllList() {
        List<BaseMenu> list = baseMenuMapper.selectList(new QueryWrapper<>());
        //根据优先级从小到大排序
        list.sort((BaseMenu h1, BaseMenu h2) -> h1.getPriority().compareTo(h2.getPriority()));
        return list;
    }

    /**
     * 根据主键获取菜单
     *
     * @param menuId
     * @return
     */
    @Override
    public BaseMenu getMenu(Long menuId) {
        return baseMenuMapper.selectById(menuId);
    }

    @Override
    public List<BaseMenu> getMenuByAppId(Long appId) {
        QueryWrapper<BaseMenu> queryWrapper = new QueryWrapper<>();
        queryWrapper.lambda().eq(BaseMenu::getAppId, appId);
        return this.selectEntitys(queryWrapper);
    }

    /**
     * 检查菜单编码是否存在
     *
     * @param menuCode
     * @return
     */
    @Override
    public Boolean isExist(String menuCode) {
        QueryWrapper<BaseMenu> queryWrapper = new QueryWrapper();
        queryWrapper.lambda()
                .eq(BaseMenu::getMenuCode, menuCode);
        int count = baseMenuMapper.selectCount(queryWrapper);
        return count > 0 ? true : false;
    }


    @Override
    public BaseMenu saveEntity(BaseMenu menu) {
        if (ObjectUtil.isNotEmpty(menu.getMenuId())) {
            BaseMenu saved = getMenu(menu.getMenuId());
            if (saved == null) {
                throw new OpenAlertException(String.format("%s信息不存在!", menu.getMenuId()));
            }
            if (!saved.getMenuCode().equals(menu.getMenuCode())) {
                // 和原来不一致重新检查唯一性
                if (isExist(menu.getMenuCode())) {
                    throw new OpenAlertException(String.format("%s编码已存在!", menu.getMenuCode()));
                }
            }
        } else {
            if (isExist(menu.getMenuCode())) {
                throw new OpenAlertException(String.format("%s编码已存在!", menu.getMenuCode()));
            }
        }
        if (StrUtil.isEmpty(menu.getScheme())) {
            menu.setScheme("/");
        }
        if (StrUtil.isEmpty(menu.getPath())) {
            menu.setPath("");
        }
        if (StrUtil.isEmpty(menu.getTarget())) {
            menu.setTarget("_self");
        }
        if (ObjectUtil.isEmpty(menu.getStatus())) {
            menu.setStatus(1);
        }
        if (ObjectUtil.isEmpty(menu.getParentId())) {
            menu.setParentId(0l);
        }
        if (ObjectUtil.isEmpty(menu.getPriority())) {
            menu.setPriority(0);
        }
        menu.setServiceId(DEFAULT_SERVICE_ID);
        menu.setCreateTime(new Date());
        menu.setUpdateTime(menu.getCreateTime());
        super.saveEntity(menu);
        // 同步权限表里的信息
        baseAuthorityService.saveOrUpdateAuthority(menu.getMenuId(), ResourceType.menu);

        return menu;
    }

    @Override
    public boolean deleteEntity(BaseMenu menu) {
        try {
            this.removeMenu(menu.getMenuId());
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * 添加菜单资源
     *
     * @param menu
     * @return
     */
    @Override
    public BaseMenu addMenu(BaseMenu menu) {
        return this.saveEntity(menu);
    }

    /**
     * 修改菜单资源
     *
     * @param menu
     * @return
     */
    @Override
    public BaseMenu updateMenu(BaseMenu menu) {
        return this.saveEntity(menu);
    }


    /**
     * 移除菜单
     *
     * @param menuId
     * @return
     */
    @Override
    public void removeMenu(Long menuId) {
        BaseMenu menu = getMenu(menuId);
        if (menu != null && menu.getIsPersist().equals(BaseConstants.ENABLED)) {
            throw new OpenAlertException(String.format("保留数据,不允许删除!"));
        }
        // 移除菜单权限
        baseAuthorityService.removeAuthority(menuId, ResourceType.menu);
        // 移除功能按钮和相关权限
        baseActionService.removeByMenuId(menuId);
        // 移除菜单信息
        baseMenuMapper.deleteById(menuId);
    }


}
