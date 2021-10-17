package com.liu.poi.utils.excel;


import java.util.List;

/**
 * @author ly
 * @date 2019/7/11
 * excel读取结果
 */
public class ExcelReadResult<T> {
    /**
     * 成功读取到的实体类列表
     */
    private List<T> result;
    /**
     * 读取失败的信息
     */
    private List<ExcelErrorLog> errorLogs;

    public List<T> getResult() {
        return result;
    }

    public void setResult(List<T> result) {
        this.result = result;
    }

    public List<ExcelErrorLog> getErrorLogs() {
        return errorLogs;
    }

    public void setErrorLogs(List<ExcelErrorLog> errorLogs) {
        this.errorLogs = errorLogs;
    }
}
