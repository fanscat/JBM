package com.jbm.cluster.auth.configuration;

import com.jbm.cluster.auth.integration.IntegrationAuthenticationFilter;
import com.jbm.cluster.common.exception.JbmOAuth2WebResponseExceptionTranslator;
import com.jbm.cluster.common.security.JbmTokenEnhancer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.oauth2.config.annotation.configurers.ClientDetailsServiceConfigurer;
import org.springframework.security.oauth2.config.annotation.web.configuration.AuthorizationServerConfigurerAdapter;
import org.springframework.security.oauth2.config.annotation.web.configuration.EnableAuthorizationServer;
import org.springframework.security.oauth2.config.annotation.web.configurers.AuthorizationServerEndpointsConfigurer;
import org.springframework.security.oauth2.config.annotation.web.configurers.AuthorizationServerSecurityConfigurer;
import org.springframework.security.oauth2.provider.ClientDetailsService;
import org.springframework.security.oauth2.provider.code.AuthorizationCodeServices;
import org.springframework.security.oauth2.provider.code.InMemoryAuthorizationCodeServices;
import org.springframework.security.oauth2.provider.token.AccessTokenConverter;
import org.springframework.security.oauth2.provider.token.DefaultTokenServices;
import org.springframework.security.oauth2.provider.token.TokenEnhancer;
import org.springframework.security.oauth2.provider.token.TokenStore;
import org.springframework.security.oauth2.provider.token.store.redis.RedisTokenStore;

/**
 * 平台认证服务器配置
 *
 * @author wesley.zhang
 */
@Configuration
@EnableAuthorizationServer
public class AuthorizationServerConfiguration extends AuthorizationServerConfigurerAdapter {
    @Autowired
    private AuthenticationManager authenticationManager;
    //    @Autowired
//    private DataSource dataSource;
    @Autowired
    private RedisConnectionFactory redisConnectionFactory;
    @Autowired
    @Qualifier(value = "integrationUserDetailsService")
    private UserDetailsService userDetailsService;
    @Autowired
    private IntegrationAuthenticationFilter integrationAuthenticationFilter;
    /**
     * 自定义获取客户端,为了支持自定义权限,
     */
    @Autowired
    @Qualifier(value = "clientDetailsServiceImpl")
    private ClientDetailsService customClientDetailsService;

    /**
     * 令牌存放
     *
     * @return
     */
    @Bean
    @Primary
    public TokenStore tokenStore() {
        return new RedisTokenStore(redisConnectionFactory);
    }


    /**
     * 授权store
     *
     * @return
     */
//    @Bean
//    public ApprovalStore approvalStore() {
//        return new JdbcApprovalStore(dataSource);
//    }

    /**
     * 令牌信息拓展
     *
     * @return
     */
    @Bean
    public TokenEnhancer tokenEnhancer() {
        return new JbmTokenEnhancer();
    }

//    /**
//     * 授权码
//     *
//     * @return
//     */
//    @Bean
//    public AuthorizationCodeServices authorizationCodeServices() {
//        return new JdbcAuthorizationCodeServices(dataSource);
//    }

    /**
     * 设置授权码模式的授权码如何存取，暂时采用内存方式
     */
    @Bean
    public AuthorizationCodeServices authorizationCodeServices() {
        return new InMemoryAuthorizationCodeServices();
    }


    @Override
    public void configure(ClientDetailsServiceConfigurer clients) throws Exception {
        clients.withClientDetails(customClientDetailsService);
    }

    /**
     * 加载默认的token解析器
     */
    @Autowired
    private AccessTokenConverter accessTokenConverter;

    @Override
    public void configure(AuthorizationServerEndpointsConfigurer endpoints) throws Exception {
        endpoints
                .allowedTokenEndpointRequestMethods(HttpMethod.GET, HttpMethod.POST)
                .authenticationManager(authenticationManager)
//                .approvalStore(approvalStore())
                .userDetailsService(userDetailsService)
                .tokenServices(createDefaultTokenServices())
                .accessTokenConverter(accessTokenConverter)
                //JDBC的code授权
//                .authorizationCodeServices(authorizationCodeServices());
                // 自定义确认授权页面
//        endpoints.pathMapping("/oauth/confirm_access", "/oauth/confirm_access");
                // 自定义错误页
//        endpoints.pathMapping("/oauth/error", "/oauth/error");
                // 自定义异常转换类
                .exceptionTranslator(new JbmOAuth2WebResponseExceptionTranslator());
    }

    private DefaultTokenServices createDefaultTokenServices() {
        DefaultTokenServices tokenServices = new DefaultTokenServices();
        tokenServices.setTokenStore(tokenStore());
        tokenServices.setTokenEnhancer(tokenEnhancer());
        tokenServices.setSupportRefreshToken(true);
        tokenServices.setReuseRefreshToken(true);
        // 令牌默认有效期2小时
        tokenServices.setAccessTokenValiditySeconds(7200);
        // 刷新令牌默认有效期3天
        tokenServices.setRefreshTokenValiditySeconds(259200);
        tokenServices.setClientDetailsService(customClientDetailsService);
        return tokenServices;
    }

    @Override
    public void configure(AuthorizationServerSecurityConfigurer security) throws Exception {
        security
                // 开启/oauth/check_token验证端口认证权限访问
//                .checkTokenAccess("isAuthenticated()")
                .checkTokenAccess("permitAll()")
                // oauth/token_key 公开密钥
                .tokenKeyAccess("permitAll()")
                .allowFormAuthenticationForClients()
                .addTokenEndpointAuthenticationFilter(integrationAuthenticationFilter);
    }

}
