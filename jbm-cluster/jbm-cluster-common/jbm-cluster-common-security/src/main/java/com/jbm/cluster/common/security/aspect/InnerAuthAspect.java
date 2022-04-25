package com.jbm.cluster.common.security.aspect;

import cn.hutool.core.util.StrUtil;
import com.jbm.cluster.core.constant.JbmSecurityConstants;
import com.jbm.cluster.common.security.annotation.InnerAuth;
import com.jbm.framework.exceptions.InnerAuthException;
import com.jbm.framework.mvc.ServletUtils;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.core.Ordered;
import org.springframework.stereotype.Component;

/**
 * 内部服务调用验证处理
 * 
 * @author ruoyi
 */
@Aspect
@Component
public class InnerAuthAspect implements Ordered
{
    @Around("@annotation(innerAuth)")
    public Object innerAround(ProceedingJoinPoint point, InnerAuth innerAuth) throws Throwable
    {
        String source = ServletUtils.getRequest().getHeader(JbmSecurityConstants.FROM_SOURCE);
        // 内部请求验证
        if (!StrUtil.equals(JbmSecurityConstants.INNER, source))
        {
            throw new InnerAuthException("没有内部访问权限，不允许访问");
        }

        String userid = ServletUtils.getRequest().getHeader(JbmSecurityConstants.DETAILS_USER_ID);
        String username = ServletUtils.getRequest().getHeader(JbmSecurityConstants.DETAILS_USERNAME);
        // 用户信息验证
        if (innerAuth.isUser() && (StrUtil.isEmpty(userid) || StrUtil.isEmpty(username)))
        {
            throw new InnerAuthException("没有设置用户信息，不允许访问 ");
        }
        return point.proceed();
    }

    /**
     * 确保在权限认证aop执行前执行
     */
    @Override
    public int getOrder()
    {
        return Ordered.HIGHEST_PRECEDENCE + 1;
    }
}
