package com.liu.gateway.gateway.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;

@Configuration
public class WebSecurityConfig extends WebSecurityConfigurerAdapter {


    @Override
    protected void configure(HttpSecurity httpSecurity) throws Exception {
        // 任何请求需要身份认证
        httpSecurity
                .authorizeRequests()
                .antMatchers("/**").permitAll()
                .and().csrf().disable();
    }
}
