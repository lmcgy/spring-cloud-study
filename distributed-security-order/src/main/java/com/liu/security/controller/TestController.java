package com.liu.security.controller;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class TestController {


    @RequestMapping("/login-success")
    public String loginSuccess(){
        String username = getUsername();
        return username+"登录成功";
    }

    /*** 获取当前登录用户名 * @return */
    private String getUsername() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (!authentication.isAuthenticated()) {
            return null;
        }
        Object principal = authentication.getPrincipal();
        String username = null;
        if (principal instanceof org.springframework.security.core.userdetails.UserDetails) {
            username = ((org.springframework.security.core.userdetails.UserDetails) principal).getUsername();
        } else {
            username = principal.toString();
        }
        return username;
    }

    //方法授权
    @GetMapping("/test/one")
    @PreAuthorize("hasAuthority('p1')")
    public String one(){
        return "访问资源1";
    }

    @GetMapping("/test/two")
    @PreAuthorize("hasAuthority('p2')")
    public String two(){
        return "访问资源2";
    }
}
