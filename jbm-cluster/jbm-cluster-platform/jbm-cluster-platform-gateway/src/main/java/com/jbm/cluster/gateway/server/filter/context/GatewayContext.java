package com.jbm.cluster.gateway.server.filter.context;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.springframework.http.HttpHeaders;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

/**
 * @author wesley.zhang
 */
@Getter
@Setter
@ToString
public class GatewayContext {

    public static final String CACHE_GATEWAY_CONTEXT = "cacheGatewayContext";
    /**
     * cache json body
     */
    private String requestBody;
    /**
     * cache Response Body
     */
    private Object responseBody;
    /**
     * request headers
     */
    private HttpHeaders requestHeaders;
    /**
     * cache form result
     */
    private MultiValueMap<String, String> formData;
    /**
     * cache all request result include:form result and query param
     */
    private MultiValueMap<String, String> allRequestData = new LinkedMultiValueMap<>(0);

}
