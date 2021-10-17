package com.liu.swagger.controller;


import com.liu.swagger.entity.User;
import io.swagger.annotations.*;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/hello")
@Api(tags = "初始化hello接口")
public class HelloController {


    @ApiOperation(value="getHello", notes="测试redis 设置值")
    @GetMapping("/getHello")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "username", value = "用户名", defaultValue = "李四"),
            @ApiImplicitParam(name = "password", value = "用户地址", defaultValue = "深圳", required = true)}
    )
    public String getHello(String username,String password){
        return "success";
    }

    @ApiOperation(value="postHello", notes="测试redis 设置值")
    @PostMapping("/postHello")
    public String postHello(@RequestBody User user){
        return "success";
    }


}
