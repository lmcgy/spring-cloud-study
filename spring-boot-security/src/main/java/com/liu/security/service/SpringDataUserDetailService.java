package com.liu.security.service;

import com.liu.security.Dao.UserDao;
import com.liu.security.model.UserDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.List;


/**
 * 自定义UserDetailsService
 */
@Service
public class SpringDataUserDetailService implements UserDetailsService {

    @Autowired
    UserDao userDao;


    @Override
    public UserDetails loadUserByUsername(String userName) throws UsernameNotFoundException {
        //登录账号
        System.out.println("username="+userName); //根据账号去数据库查询...
        UserDto user = userDao.getUserByUsername(userName); if(user == null){ return null; }
        //查询用户权限
        List<String> permissions = userDao.findPermissionsByUserId(user.getId());
        String[] perarray = new String[permissions.size()];
        permissions.toArray(perarray);
        //创建userDetails
        UserDetails userDetails = User.withUsername(user.getFullname()).password(user.getPassword()).authorities(perarray).build();
        return userDetails;
    }
}
