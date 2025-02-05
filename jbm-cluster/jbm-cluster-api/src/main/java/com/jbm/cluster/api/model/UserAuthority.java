package com.jbm.cluster.api.model;

import com.google.common.collect.Lists;
import com.jbm.cluster.common.security.OpenAuthority;

import java.io.Serializable;
import java.util.Collection;
import java.util.Map;

/**
 * @author: wesley.zhang
 * @date: 2018/11/12 11:35
 * @description:
 */
public class UserAuthority implements Serializable {
    private static final long serialVersionUID = 6717800085953996702L;

    private Collection<Map> roles = Lists.newArrayList();
    /**
     * 用户权限
     */
    private Collection<OpenAuthority> authorities = Lists.newArrayList();


    public Collection<OpenAuthority> getAuthorities() {
        return authorities;
    }

    public void setAuthorities(Collection<OpenAuthority> authorities) {
        this.authorities = authorities;
    }

    public Collection<Map> getRoles() {
        return roles;
    }

    public void setRoles(Collection<Map> roles) {
        this.roles = roles;
    }
}
