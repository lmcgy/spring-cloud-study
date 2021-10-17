package com.liu.poi.template;


import com.liu.poi.utils.excel.BaseExcelImportTemplateProtocol;
import com.liu.poi.utils.excel.ExcelElement;
import lombok.Data;

import java.util.Date;

@Data
public class ExportTemplate  extends BaseExcelImportTemplateProtocol {

    @ExcelElement(fieldName = "主键")
    private Integer id;


    @ExcelElement(fieldName = "姓名")
    private String name;


    @ExcelElement(fieldName = "时间")
    private Date time;


    @ExcelElement(fieldName = "金钱")
    private Double money;
}
