package com.jbm.cluster.api.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.jbm.cluster.api.model.entity.BaseApi;
import com.jbm.util.StringUtils;

import java.io.Serializable;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

/**
 * @author wesley.zhang
 */
public class IpLimitApi extends BaseApi implements Serializable {
    private static final long serialVersionUID = 1212925216631391016L;
    private Long itemId;
    private Long policyId;
    private String policyName;
    private Integer policyType;

    @JsonIgnore
    private String ipAddress;

    private Set<String> ipAddressSet ;

    public String getIpAddress() {
        return ipAddress;
    }

    public void setIpAddress(String ipAddress) {
        this.ipAddress = ipAddress;
        if(StringUtils.isNotBlank(ipAddress)){
            ipAddressSet =  new HashSet(Arrays.asList(ipAddress.split(";")));
        }
    }

    public Set<String> getIpAddressSet() {
        return ipAddressSet;
    }

    public void setIpAddressSet(Set<String> ipAddressSet) {
        this.ipAddressSet = ipAddressSet;
    }

    public Integer getPolicyType() {
        return policyType;
    }

    public void setPolicyType(Integer policyType) {
        this.policyType = policyType;
    }

    public Long getItemId() {
        return itemId;
    }

    public void setItemId(Long itemId) {
        this.itemId = itemId;
    }

    public Long getPolicyId() {
        return policyId;
    }

    public void setPolicyId(Long policyId) {
        this.policyId = policyId;
    }

    public String getPolicyName() {
        return policyName;
    }

    public void setPolicyName(String policyName) {
        this.policyName = policyName;
    }
}
