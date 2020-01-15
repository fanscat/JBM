package com.jbm.cluster.common.security.oauth2.client;

import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.Map;

/**
 * @author: wesley.zhang
 * @date: 2019/2/14 14:34
 * @description:
 */
@ConfigurationProperties(prefix = "jbm.client")
public class OpenOAuth2ClientProperties {

    private Map<String, OpenOAuth2ClientDetails> oauth2;

    public Map<String, OpenOAuth2ClientDetails> getOauth2() {
        return oauth2;
    }

    public void setOauth2(Map<String, OpenOAuth2ClientDetails> oauth2) {
        this.oauth2 = oauth2;
    }
}
