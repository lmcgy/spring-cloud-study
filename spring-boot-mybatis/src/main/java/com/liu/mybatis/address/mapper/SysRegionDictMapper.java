package com.liu.mybatis.address.mapper;

import com.liu.mybatis.address.entity.SysRegionDict;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.mybatis.spring.annotation.MapperScan;

/**
 * <p>
 * 地区字典表 Mapper 接口
 * </p>
 *
 * @author jobob
 * @since 2020-12-10
 */
@Mapper
public interface SysRegionDictMapper extends BaseMapper<SysRegionDict> {

}
