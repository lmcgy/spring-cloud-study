package com.liu.mybatis.address.entity;

import java.time.LocalDateTime;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.extension.activerecord.Model;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * <p>
 * 地区字典表
 * </p>
 *
 * @author jobob
 * @since 2020-12-10
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("sys_region_dict")
public class SysRegionDict extends Model<SysRegionDict> {

    private static final long serialVersionUID = 1L;

    @TableId
    private Long id;

    /**
     * 中心ID
     */
    @TableField
    private Long centerId;

    /**
     * 地区编码
     */
    @TableField
    private String regionCode;

    /**
     * 城市编码
     */
    @TableField
    private String cityCode;

    /**
     * 地区名称
     */
    @TableField
    private String regionName;

    /**
     * 上级地区ID
     */
    @TableField
    private Long parentRegionId;

    /**
     * 地区类型(1省|2市|3区)
     */
    @TableField
    private Integer regionType;

    /**
     * 地区层级（省1市2区3）
     */
    @TableField
    private Integer regionLevel;

    /**
     * 备注
     */
    @TableField
    private String remark;

    /**
     * 是否已删除 0：否 1：已删除
     */
    @TableField
    private Boolean isRemove;

    /**
     * 创建人ID
     */
    @TableField
    private Long createUserId;

    /**
     * 创建时间
     */
    @TableField
    private LocalDateTime createTime;

    /**
     * 修改人ID
     */
    @TableField
    private Long updateUserId;

    /**
     * 修改时间
     */
    @TableField
    private LocalDateTime updateTime;

    /**
     * 删除人ID
     */
    @TableField
    private Long deleteUserId;

    /**
     * 删除时间
     */
    @TableField
    private LocalDateTime deleteTime;

    /**
     * 版本号
     */
    @TableField
    private Integer version;


}
