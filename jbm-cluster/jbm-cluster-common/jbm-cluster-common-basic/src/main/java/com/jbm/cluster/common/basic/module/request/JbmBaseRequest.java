package com.jbm.cluster.common.basic.module.request;

import cn.hutool.core.net.url.UrlBuilder;
import cn.hutool.http.ContentType;
import cn.hutool.http.HttpRequest;
import cn.hutool.http.HttpResponse;
import cn.hutool.http.Method;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpMethod;

@Slf4j
public abstract class JbmBaseRequest implements ICustomizeRequest {

    @Override
    public HttpResponse request(String url, String methodType, String jsonBody) {
        HttpRequest httpRequest = new HttpRequest(this.buildUrl(url));
        httpRequest.body(jsonBody);
        httpRequest.setMethod(Method.GET);
        if (HttpMethod.POST.matches(methodType)) {
            httpRequest.setMethod(Method.POST);
        }
        return request(httpRequest);
    }

    @Override
    public HttpResponse request(HttpRequest httpRequest) {
        httpRequest.contentType(ContentType.JSON.getValue());
        httpRequest = this.buildRequest(httpRequest);
        HttpResponse httpResponse = httpRequest.execute(true);
        log.info("执行URL状态为[{}],结果[{}]", httpResponse.getStatus(), httpResponse.body());
        return httpResponse;
    }


    public HttpRequest buildRequest(HttpRequest httpRequest) {
        return httpRequest;
    }

    public UrlBuilder buildUrl(String sourceUrl) {
        return UrlBuilder.of(sourceUrl);
    }


}
