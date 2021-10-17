package com.liu.swagger.entity;


import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

import javax.validation.constraints.NotNull;

@ApiModel("用户实体类")
public class User {

    @NotNull(message = "用户名不能为空")
    @ApiModelProperty(value = "刘苗",name = "用户名",required = true)
    private String name;

    @ApiModelProperty("用户密码")
    private String password;

}
