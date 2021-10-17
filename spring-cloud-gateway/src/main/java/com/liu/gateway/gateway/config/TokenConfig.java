package com.liu.gateway.gateway.config;


import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.oauth2.provider.token.TokenStore;
import org.springframework.security.oauth2.provider.token.store.JwtAccessTokenConverter;
import org.springframework.security.oauth2.provider.token.store.JwtTokenStore;

@Configuration
public class TokenConfig {

    private String SIGNING_KEY = "uaa123";

    //内存模式，用户以及权限信息是存储在服务器上，而且资源服务需要进行远程的token校验
    /*@Bean
    public TokenStore tokenStore(){
        return new InMemoryTokenStore();
    }*/


    //jwt已经包含了用户信息，不需要进行，在资源服务器上面可以根据 资源服务器使用该秘钥来验证
    @Bean
    public TokenStore tokenStore() {
        return new JwtTokenStore(accessTokenConverter());
    }

    @Bean
    public JwtAccessTokenConverter accessTokenConverter() {
        JwtAccessTokenConverter converter = new JwtAccessTokenConverter();
        converter.setSigningKey(SIGNING_KEY);//对称秘钥，资源服务器使用该秘钥来验证
        return converter;
    }
}
