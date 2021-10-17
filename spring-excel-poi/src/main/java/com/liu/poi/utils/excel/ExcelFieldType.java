package com.liu.poi.utils.excel;

/**
 * @author chencheng
 * @date 2018/1/25
 */
public enum ExcelFieldType {

    String("String"),
    Integer("Integer"),
    Float("Float"),
    Double("Double"),
    Long("Long"),
    Short("Short"),
    Boolean("Boolean"),
    Date("Date");

    private String name;

    ExcelFieldType(java.lang.String name) {
        this.name = name;
    }

    public java.lang.String toName() {
        return name;
    }

    public static ExcelFieldType fromName(String name){
        for (ExcelFieldType excelFieldType : values()) {
            if (excelFieldType.name.equals(name)){
                return excelFieldType;
            }
        }
        return null;
    }
}
