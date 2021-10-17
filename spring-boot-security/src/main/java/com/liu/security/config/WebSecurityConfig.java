package com.liu.security.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.NoOpPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;

@Configuration
@EnableGlobalMethodSecurity(prePostEnabled = true)  //开启这个注解才能 实用 用prePost注解的支持
public class WebSecurityConfig extends WebSecurityConfigurerAdapter {


    //因为自定义了UserDetailsService这里进行屏蔽
    /*@Bean
    public UserDetailsService userDetailsService(){
        InMemoryUserDetailsManager inMemoryUserDetailsManager = new InMemoryUserDetailsManager();
        inMemoryUserDetailsManager.createUser(User.withUsername("liu").password("123456").authorities("lo").build());
        inMemoryUserDetailsManager.createUser(User.withUsername("chen").password("123456").authorities("ou").build());
        return inMemoryUserDetailsManager;
    }*/

    /*@Bean
    public PasswordEncoder passwordEncoder(){
        return NoOpPasswordEncoder.getInstance();
    }*/
    @Bean
    public PasswordEncoder passwordEncoder(){
        return new BCryptPasswordEncoder();
    }

    @Override
    protected void configure(HttpSecurity httpSecurity) throws Exception {
        // 任何请求需要身份认证
        httpSecurity.authorizeRequests()
                //web授权
                //.antMatchers("/test/one").hasAuthority("p1")
                //.antMatchers("/test/two").hasAuthority("p2")
                .antMatchers("/test/*").authenticated()
                .anyRequest().permitAll()
                .and()
                .formLogin()
                .successForwardUrl("/login-success");

    }
}
