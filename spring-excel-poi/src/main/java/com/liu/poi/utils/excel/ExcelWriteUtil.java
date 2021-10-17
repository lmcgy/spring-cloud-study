package com.liu.poi.utils.excel;

import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.lang.reflect.Method;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ExcelWriteUtil {






    /**
     * 导出excel
     * @param outFile 目标文件（会将模板文件的标题与样式填充进来）
     * @param templateInputStream 模板文件对应的输入流（模板要求：第一行是字段行，第二行是标题行，第三行是数据行）
     * @param dataType 数据类型
     * @param datas 导出数据
     * @param startRowNum 读取数据开始行
     */
    public static <T> void write(File outFile, InputStream templateInputStream, Class<T>dataType, List<T> datas,Integer startRowNum) throws Exception {
        startRowNum = startRowNum == null ? 1 : startRowNum;
        //读取模板信息
        Workbook templateBook = WorkbookFactory.create(templateInputStream);
        Sheet templateSheet = templateBook.getSheetAt(0);
        //模板的字段信息
        List<ExcelField> excelFields = ExcelBaseUtil.getExcelFields(templateSheet,dataType,startRowNum - 1 );
        templateBook.close();
        templateInputStream.close();

        //创建工作表
        Workbook workbook = createWorkbook(outFile);
        Sheet sheet = workbook.createSheet();
        //输出标题、设置列宽
        createTitle(workbook, sheet, excelFields);

        //输出数据
        for(int dataIndex = 0; dataIndex < datas.size(); dataIndex++){
            //创建一行，因为第一行是标题，所以行数加1
            Row row = sheet.createRow(dataIndex + 1);
            T data = datas.get(dataIndex);
            for (ExcelField excelField : excelFields) {
                //创建 单元格
                Cell cell = row.createCell(excelField.getColumnIndex());

                //调用get方法取数据
                Method getMethod =excelField.getMethod();
                if (getMethod == null){
                    continue;
                }
                Object value = getMethod.invoke(data);
                if (value == null){
                    continue;
                }
                Class<?> valueType = getMethod.getReturnType();
                setCellValue(cell, valueType.getName(), value);
            }
        }

        OutputStream outputStream = new FileOutputStream(outFile);
        workbook.write(outputStream);
        workbook.close();
        outputStream.close();
    }


    /**
     * 直接导出excel
     * @param dataType 类型
     * @param datas 导出数据
     */
    public static <T> void writeForOut(Class<T> dataType, List<Map<String, Object>> datas, HttpServletResponse response) throws Exception {
        Integer startRowNum = null;
        startRowNum = startRowNum == null ? 1 : startRowNum;
        //创建工作表
        Workbook workbook = new XSSFWorkbook();
        Sheet sheet = workbook.createSheet();
        //模板的字段信息   要得到 标题行   为读取数据行号 - 1
        List<ExcelField> excelFields = ExcelBaseUtil.getExcelFields(sheet,dataType,startRowNum - 1);

        //输出标题、设置列宽
        createTitle(workbook, sheet, excelFields);

        //输出数据
        for(int dataIndex = 0; dataIndex < datas.size(); dataIndex++){
            //创建一行，因为第一行是标题，所以行数加1
            Row row = sheet.createRow(dataIndex + 1);

            Map<String, Object> dataMap = datas.get(dataIndex);
            for (ExcelField excelField : excelFields) {
                //创建单元格
                Cell cell = row.createCell(excelField.getColumnIndex());
                //通过对应的字段编码获取对应的值
                Object value = dataMap.get(excelField.getFieldCode());
                if (value == null){
                    continue;
                }
                Class<?> valueType = value.getClass();
                setCellValue(cell, valueType.getName(), value);


            }
        }

        response.setContentType("application/vnd.ms-excel");
        response.setHeader("Content-Disposition", "attachment; filename=test.xlsx");

        OutputStream outputStream = response.getOutputStream();
        workbook.write(outputStream);
        workbook.close();
        outputStream.flush();
        outputStream.close();
    }

    /**
     * 导出excel
     * @param outFile 目标文件（会将模板文件的标题与样式填充进来）
     * @param dataType 类型
     * @param datas 导出数据
     * @param startRowNum 读取数据开始行
     */
    public static <T> void write(File outFile,Class<T> dataType,List<Map<String, Object>> datas,Integer startRowNum) throws Exception {
        startRowNum = startRowNum == null ? 1 : startRowNum;
        //创建工作表
        Workbook workbook = createWorkbook(outFile);
        Sheet sheet = workbook.createSheet();
        //模板的字段信息   要得到 标题行   为读取数据行号 - 1
        List<ExcelField> excelFields = ExcelBaseUtil.getExcelFields(sheet,dataType,startRowNum - 1);

        //输出标题、设置列宽
        createTitle(workbook, sheet, excelFields);

        //输出数据
        for(int dataIndex = 0; dataIndex < datas.size(); dataIndex++){
            //创建一行，因为第一行是标题，所以行数加1
            Row row = sheet.createRow(dataIndex + 1);

            Map<String, Object> dataMap = datas.get(dataIndex);
            for (ExcelField excelField : excelFields) {
                //创建单元格
                Cell cell = row.createCell(excelField.getColumnIndex());
                //通过对应的字段编码获取对应的值
                Object value = dataMap.get(excelField.getFieldCode());
                if (value == null){
                    continue;
                }
                Class<?> valueType = value.getClass();
                setCellValue(cell, valueType.getName(), value);
            }
        }

        OutputStream outputStream = new FileOutputStream(outFile);
        workbook.write(outputStream);
        workbook.close();
        outputStream.flush();
        outputStream.close();
    }


    /**
     * 根据错误日志导出excel（把错误数据输出到源文件）
     * @param formFile 读取目标（错误信息对应的来源数据，第一行是字段行，第二行是标题行，第三行开始是数据）
     * @param errorLogs 错误列表（错误行数及错误信息）
     */
    public static void writeByErrorLog(File formFile, List<ExcelErrorLog> errorLogs) throws Exception {
        writeByErrorLog(formFile, formFile, errorLogs);
    }


    public static void writeByErrorLog(File outFile, File formFile, List<ExcelErrorLog> errorLogs)throws Exception{
        Map<Integer, String> errorMap = new HashMap<>(errorLogs.size());
        for (ExcelErrorLog errorLog : errorLogs) {
            errorMap.put(errorLog.getRowNum(), errorLog.getErrorMessage());
        }

        InputStream inputStream = new FileInputStream(formFile);
        Workbook workbook = WorkbookFactory.create(inputStream);
        Sheet sheet = workbook.getSheetAt(0);//得到默认第一个工作表
        Row fieldRow = sheet.getRow(0);
        short lastCellNum = fieldRow.getLastCellNum();
        int lastRowNum = sheet.getLastRowNum();
        sheet.setColumnWidth(lastCellNum, 100*256);
        //第一行是标题行，第二行开始才是数据行，所以rowNum从1开始
        for(int rowNum = 1; rowNum <= lastRowNum ; rowNum++){
            Row row = sheet.getRow(rowNum);
            String errorMessage = errorMap.get(rowNum);
            if (null == errorMessage){
                //如果该行没错就删除
                sheet.removeRow(row);
            }else {
                //有错就保留并在最后追加错误信息
                Cell cell = row.createCell(lastCellNum);
                cell.setCellValue(errorMessage);
            }
        }

        //清除空行
        ExcelBaseUtil.clearBlankRow(sheet);
        //先把inputStream关闭，再创建outputStream，因为有时候输入输出都在同一个文件
        inputStream.close();
        FileOutputStream outputStream = new FileOutputStream(outFile);
        workbook.write(outputStream);
        workbook.close();
        outputStream.close();
    }


    private static <T> void setCellValue(Cell cell , String valueType, Object value){

        switch (valueType) {
            case "java.lang.Integer":
            case "int":
                cell.setCellValue((Integer)value);
                break;
            case "java.lang.Double":
            case "double":
                cell.setCellValue((Double) value);
                break;
            case "java.lang.Float":
            case "float":
                cell.setCellValue((Float)value);
                break;
            case "java.lang.Long":
            case "long":
                cell.setCellValue((Long)value);
                break;
            case "java.lang.Short":
            case "short":
                cell.setCellValue((Short)value);
                break;
            case "java.lang.Boolean":
            case "boolean":
                cell.setCellValue((Boolean) value);
                break;
            case "java.util.Date":
                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
                String time= sdf.format(value);
                cell.setCellValue(time);
                break;
            default:
                cell.setCellValue((String) value);
        }
    }

    /**
     * 创建标题
     * @param workbook
     * @param sheet
     * @param excelFields
     */
    private static void createTitle(Workbook workbook, Sheet sheet, List<ExcelField> excelFields){
        Row titleRow = sheet.createRow(0);//获取表格第一行
        for (ExcelField excelField : excelFields) {
            Integer columnIndex = excelField.getColumnIndex();
            Cell titleCell = titleRow.createCell(columnIndex);
            titleCell.setCellValue(excelField.getFieldName());//设置单元格标题
            //拷贝单元格样式
           /* CellStyle titleCellStyle = workbook.createCellStyle();//初始化单元格格式对象
            ExcelBaseUtil.copyCellStyle(titleCellStyle, excelField.getTitleStyle());
            titleCell.setCellStyle(titleCellStyle);
            sheet.setColumnWidth(columnIndex, excelField.getColumnWidth());*/
        }
    }

    /**
     * 创建工作部
     * @param outFile
     * @return
     */
    private static Workbook createWorkbook(File outFile){
        //创建工作表
        Workbook workbook = null;
        if (outFile.getName().endsWith(".xlsx")){
            workbook = new XSSFWorkbook();
        }else {
            workbook = new HSSFWorkbook();
        }

        return workbook;
    }
}
