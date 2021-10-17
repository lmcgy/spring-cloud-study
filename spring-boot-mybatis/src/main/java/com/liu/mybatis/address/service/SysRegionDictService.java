package com.liu.mybatis.address.service;

import com.liu.mybatis.address.entity.SysRegionDict;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;


/**
 * <p>
 * 地区字典表 服务类
 * </p>
 *
 * @author jobob
 * @since 2020-12-10
 */
public interface SysRegionDictService extends IService<SysRegionDict> {


    void saveRegionDict();

    void task(List<Integer> exportVehicleRelationDtos);
}
