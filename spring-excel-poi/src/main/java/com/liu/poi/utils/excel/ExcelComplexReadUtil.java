package com.liu.poi.utils.excel;

import org.apache.commons.lang3.StringUtils;
import org.apache.poi.ss.usermodel.*;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * 复杂格式（合并单元格） excel 导入读取
 * @author ly
 * @date 2019/8/14 19:05
 */
public class ExcelComplexReadUtil {

    /**
     * 读取第一个工作表，第一行是字段行，第二行是标题行，数据从第三行开始，读取所有行
     * @param <T> 返回的数据类型
     * @param readFile 读取的excel文件
     * @param resultType 读取数据后需要转换为的bean的类型。
     * @param startRowNum 开始读取行
     */
    public static <T> ExcelReadResult<T> read(File readFile, Class<T> resultType, Integer startRowNum) throws Exception {
        if(startRowNum == null){
            startRowNum = 1;
        }

        int sheetIndex = 0;//第0个工作表
        int endRowNum = -1;
        return read(readFile, resultType, sheetIndex, startRowNum, endRowNum);
    }

    /**
     * 自定义读取
     * @param <T> 返回的数据类型
     * @param readFile 读取的excel文件
     * @param resultType 读取数据后需要转换为的bean的类型。bean类需要有无参构造方法且bean中的字段只支持以下类型：基本数据类型、日期
     * @param sheetIndex 读取哪个工作表，下标从零开始
     * @param startRowNum 从第几行开始读取，行数从0开始算
     * @param endRowNum 读取到第几行（包括），行数从0开始算，负数表示读取到结束
     */
    public static <T> ExcelReadResult<T> read(File readFile, Class<T> resultType, int sheetIndex, int startRowNum, int endRowNum) throws Exception {
        InputStream inputStream = new FileInputStream(readFile);
        Workbook workbook = WorkbookFactory.create(inputStream);
        Sheet sheet = workbook.getSheetAt(sheetIndex);
        int lastRowNum = ExcelAnalysisUtil.getNum(sheet);//获取有效行
        if (endRowNum < 0 || endRowNum > lastRowNum){
            endRowNum = lastRowNum;
        }
        FormulaEvaluator evaluator = workbook.getCreationHelper().createFormulaEvaluator();
        ExcelReadResult<T> excelReadResult = readToList(resultType, sheet, startRowNum, endRowNum, evaluator);
        workbook.close();
        inputStream.close();
        return excelReadResult;
    }

    /**
     * 单元格数据读取为 指定指定的类型 ExcelReadResult 对象
     * @param resultType 泛型 Class
     * @param sheet 数据表
     * @param dateStartRowNum 数据读取开始行号
     * @param dateEndRowNum 数据结束行号
     * @param formulaEvaluator
     * @param <T>
     * @return
     * @throws Exception
     */
    private static <T> ExcelReadResult<T> readToList(Class<T> resultType, Sheet sheet, int dateStartRowNum, int dateEndRowNum, FormulaEvaluator formulaEvaluator) throws Exception {
        //获取表格字段信息

        int titleRowNum = dateStartRowNum - 1;
        List<ExcelField> excelFields = ExcelBaseUtil.getExcelFields(sheet , resultType , titleRowNum);
        //java 8
        Map<Integer,ExcelField> excelFieldMap = excelFields.stream().collect(Collectors.toMap(ExcelField::getColumnIndex, Function.identity()));

        //读取数据到实体类
        List<T> results = new ArrayList<>();
        List<ExcelErrorLog> errorLogs = new ArrayList<>();
        Row row;
        for (int r = dateStartRowNum; r <= dateEndRowNum; r++){//遍历当前所有行
            row = sheet.getRow(r);
            if (row == null){
                continue;
            }
            T data = resultType.newInstance(); //创建实体对象
            List<String> rowErrors = new ArrayList<>();
            for(Cell cell : row) {
                int columnIndex = cell.getColumnIndex();// 列索引
                ExcelField excelField = excelFieldMap.get(columnIndex);
                Method method = excelField.getMethod();
                ExcelFieldType excelFieldType = ExcelFieldType.fromName(excelField.getFieldType());// 字段类型
                Object fieldValue = null ;
                try{
                    if(ExcelAnalysisUtil.isMergedRegion(sheet, r, columnIndex)){//合并的单元格
                        fieldValue = ExcelAnalysisUtil.getMergedRegionValue(sheet, row.getRowNum(), columnIndex,excelFieldType,formulaEvaluator);
                    }else{
                        fieldValue = ExcelAnalysisUtil.getCellValue(cell,excelFieldType,formulaEvaluator);
                    }

                    if (fieldValue != null && method != null && data != null){
                        method.invoke(data, fieldValue);
                    }
                }catch (Exception e){
                    ExcelAnalysisUtil.addErrorLogByReadTypeError(rowErrors, excelField);
                }
            }
            ExcelAnalysisUtil.setRowNumToEntity(resultType, data, r);
            //错误信息
            if (rowErrors.size() > 0){
                errorLogs.add(new ExcelErrorLog(r, StringUtils.join(rowErrors, "、")));
            }else {
                results.add(data);
            }

        }
        ExcelReadResult<T> readResult = new ExcelReadResult<T>();
        readResult.setResult(results);
        readResult.setErrorLogs(errorLogs);
        return readResult;
    }
}
