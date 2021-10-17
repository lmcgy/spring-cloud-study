package com.liu.poi.utils.excel;

import org.apache.poi.ss.usermodel.CellStyle;

import java.lang.reflect.Method;

/**
 * @author ly
 * @date 2019/07/11
 * 对应excel某列的信息
 */
public class ExcelField {
    /**
     * 列下标
     */
    private Integer columnIndex;

    /**
     * 列宽
     */
    private Integer columnWidth;

    /**
     * 字段名
     */
    private String fieldName;

    /**
     * 字段编码
     */
    private String fieldCode;

    /**
     * 字段类型：主要是String、Integer、Long、Double、Date
     */
    private String fieldType;

    /**
     * 字段允许长度
     */
    private Integer fieldLength;

    /**
     * 是否不允许为空
     */
    private Boolean isNotNull;

    /**
     * 方法
     */
    private Method method;

    /**
     * 标题样式
     */
    private CellStyle titleStyle;

    public Integer getColumnIndex() {
        return columnIndex;
    }

    public void setColumnIndex(Integer columnIndex) {
        this.columnIndex = columnIndex;
    }

    public Integer getColumnWidth() {
        return columnWidth;
    }

    public void setColumnWidth(Integer columnWidth) {
        this.columnWidth = columnWidth;
    }

    public String getFieldName() {
        return fieldName;
    }

    public void setFieldName(String fieldName) {
        this.fieldName = fieldName;
    }

    public String getFieldCode() {
        return fieldCode;
    }

    public void setFieldCode(String fieldCode) {
        this.fieldCode = fieldCode;
    }

    public String getFieldType() {
        return fieldType;
    }

    public void setFieldType(String fieldType) {
        this.fieldType = fieldType;
    }



    public Integer getFieldLength() {
        return fieldLength;
    }

    public void setFieldLength(Integer fieldLength) {
        this.fieldLength = fieldLength;
    }

    public Boolean isNotNull() {
        return isNotNull;
    }

    public void setNotNull(Boolean isNotNull) {
        this.isNotNull = isNotNull;
    }

    public Method getMethod() {
        return method;
    }

    public void setMethod(Method method) {
        this.method = method;
    }

    public CellStyle getTitleStyle() {
        return titleStyle;
    }

    public void setTitleStyle(CellStyle titleStyle) {
        this.titleStyle = titleStyle;
    }
}
