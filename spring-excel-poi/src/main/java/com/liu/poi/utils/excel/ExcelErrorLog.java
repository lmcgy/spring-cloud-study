package com.liu.poi.utils.excel;

/**
 * @author chencheng
 * @date 2018/1/23
 * excel错误日志
 */
public class ExcelErrorLog {
    /**
     * 错误行下标（从0开始）
     */
    private Integer rowNum;
    /**
     * 错误信息
     */
    private String errorMessage;

    public ExcelErrorLog() {
    }

    public ExcelErrorLog(Integer rowNum, String errorMessage) {
        this.rowNum = rowNum;
        this.errorMessage = errorMessage;
    }

    public Integer getRowNum() {
        return rowNum;
    }

    public void setRowNum(Integer rowNum) {
        this.rowNum = rowNum;
    }

    public String getErrorMessage() {
        return errorMessage;
    }

    public void setErrorMessage(String errorMessage) {
        this.errorMessage = errorMessage;
    }
}
