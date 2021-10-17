package com.liu.mybatis.address.module;

import lombok.Data;

import java.util.List;

@Data
public class AreaDTO {

    /**
     * 城市编码
     */
    private String citycode;

    /**
     * 地区编码
     */
    private String adcode;

    /**
     * 名称
     */
    private String name;

    /**
     * 层级
     */
    private String level;

    private List<AreaDTO>  districts;

}
