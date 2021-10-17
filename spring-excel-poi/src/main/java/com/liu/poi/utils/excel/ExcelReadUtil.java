package com.liu.poi.utils.excel;


import org.apache.commons.lang3.StringUtils;
import org.apache.poi.hssf.usermodel.HSSFDataFormat;
import org.apache.poi.hssf.usermodel.HSSFDateUtil;
import org.apache.poi.hssf.util.CellReference;
import org.apache.poi.ss.usermodel.*;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;


public class ExcelReadUtil {

    public static <T> ExcelReadResult<T> read(InputStream inputStream, Class<T> resultType, int sheetIndex, int startRowNum, int endRowNum) throws Exception {
        Workbook workbook = WorkbookFactory.create(inputStream);
        Sheet sheet = workbook.getSheetAt(sheetIndex);
        int lastRowNum = getNum(sheet);
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
        return read(readFile, resultType, 0, startRowNum, -1);
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
        int lastRowNum = getNum(sheet);
        if (endRowNum < 0 || endRowNum > lastRowNum){
            endRowNum = lastRowNum;
        }
        FormulaEvaluator evaluator = workbook.getCreationHelper().createFormulaEvaluator();
        ExcelReadResult<T> excelReadResult = readToList(resultType, sheet, startRowNum, endRowNum, evaluator);
        workbook.close();
        inputStream.close();
        return excelReadResult;
    }


    private static <T> ExcelReadResult<T> readToList(Class<T> resultType, Sheet sheet, int startRowNum, int endRowNum, FormulaEvaluator formulaEvaluator) throws Exception {
        //获取表格字段信息
        List<ExcelField> excelFields =  ExcelBaseUtil.getExcelFields(sheet,resultType,startRowNum - 1 );
        //读取数据到实体类
        List<T> results = new ArrayList<>();
        List<ExcelErrorLog> errorLogs = new ArrayList<>();
        Row row = null;
        Cell cell = null;
        for (int rowNUm = startRowNum; rowNUm <= endRowNum; rowNUm++){
            row = sheet.getRow(rowNUm);
            if (null == row){
                continue;
            }
            //创建实体对象
            T data = resultType.newInstance();
            List<String> rowErrors = new ArrayList<>();
            for (ExcelField excelField : excelFields) {
                try {
                    Object fieldValue = getFieldValue(row, excelField, formulaEvaluator);
                    verify(fieldValue, excelField, rowErrors);
                    Method method = excelField.getMethod();
                    if (null != fieldValue && null != method && null != data){
                        method.invoke(data, fieldValue);
                    }
                } catch (Exception e){
                    addErrorLogByReadTypeError(rowErrors, excelField);
                }
            }
            //保存行号信息给实体对象
            setRowNumToEntity(resultType, data, rowNUm);

            //错误信息
            if (rowErrors.size() > 0){
                errorLogs.add(new ExcelErrorLog(rowNUm, StringUtils.join(rowErrors, "、")));
            }else {
                results.add(data);
            }
        }
        ExcelReadResult<T> readResult = new ExcelReadResult<T>();
        readResult.setResult(results);
        readResult.setErrorLogs(errorLogs);
        return readResult;
    }

    /**
     * 获取字段值
     * @param row
     * @param excelField
     * @param formulaEvaluator
     * @return
     */
    private static Object getFieldValue(Row row, ExcelField excelField, FormulaEvaluator formulaEvaluator){
        Cell cell = row.getCell(excelField.getColumnIndex());
        ExcelFieldType excelFieldType = ExcelFieldType.fromName(excelField.getFieldType());
        return getCellValue(cell, excelFieldType, formulaEvaluator);
    }

    /**
     * 获取单元格的值
     * @param cell
     * @param excelFieldType
     * @param formulaEvaluator
     * @return
     */
    private static Object getCellValue(Cell cell, ExcelFieldType excelFieldType, FormulaEvaluator formulaEvaluator){
        if (null == cell || null == excelFieldType){
            return null;
        }

        Object value = null;
        //根据不同的参数类型从excel中读取相应类型的数据
        switch (excelFieldType) {
            case Integer:
                value = getNumberValue(cell, formulaEvaluator).intValue();
                break;
            case Double:
                value = getNumberValue(cell, formulaEvaluator);
                break;
            case Float:
                value = getNumberValue(cell, formulaEvaluator).floatValue();
                break;
            case Long:
                value = getNumberValue(cell, formulaEvaluator).longValue();
                break;
            case Short:
                value = getNumberValue(cell, formulaEvaluator).shortValue();
                break;
            case Boolean:
                value = getBooleanValue(cell);
                break;
            case Date:
                value = getDateValue(cell);
                break;
            case String:
                value = getStringCellValue(cell);
                break;
            default:
                throw new RuntimeException();
        }
        return value;
    }

    private static List<String> trueStrings = Arrays.asList("true","yes","Yes","Y","y","是");
    private static Boolean getBooleanValue(Cell cell){
        Boolean value = null;
        switch (cell.getCellTypeEnum()){
            case BLANK:
                break;
            case BOOLEAN:
                value = cell.getBooleanCellValue();
                break;
            case  NUMERIC:
                double number = cell.getNumericCellValue();
                value = (number == 1) ;
                break;
            case STRING:
                String stringCellValue = cell.getStringCellValue();
                value = trueStrings.contains(stringCellValue);
                break;
            default:
                throw new RuntimeException();
        }
        return value;
    }

    private static Double getNumberValue(Cell cell, FormulaEvaluator formulaEvaluator){
        Double number = null;
        switch (cell.getCellTypeEnum()) {
            case BLANK:
                break;
            case NUMERIC:
                number = cell.getNumericCellValue();
                break;
            case STRING:
                number = Double.valueOf(cell.getStringCellValue());
                break;
            case FORMULA:
                number = formulaEvaluator.evaluate(cell).getNumberValue();
                break;
            default:
                throw new RuntimeException();
        }
        return number;
    }

    /**
     * 初始化一个日期格式化列表，可根据需要自己添加
     */
    private static String[] datePatterns = {"yyyy/MM/dd HH:mm:ss", "yyyy-MM-dd HH:mm:ss", "yyyy年MM月dd日 HH:mm:ss", "yyyy/MM/dd",  "yyyy-MM-dd", "yyyy年MM月dd日", "yyyy.MM.dd."};
    private static List<SimpleDateFormat> dateFormats = new ArrayList<>();
    static {
        for (String datePattern : datePatterns) {
            dateFormats.add(new SimpleDateFormat(datePattern));
        }
    }
    private static Date getDateValue(Cell cell){
        Date date = null;
        switch (cell.getCellTypeEnum()) {
            case BLANK:
                break;
            case STRING:
                //如果单元格格式是字符串类型，将会尝试格式化成日期
                String dateStr = cell.getStringCellValue();
                for (SimpleDateFormat dateFormat : dateFormats) {
                    try {
                        date = dateFormat.parse(dateStr);
                        break;
                    } catch (ParseException ignored) {
                    }
                }
                if (null == date){
                    throw new RuntimeException();
                }
                break;
            case NUMERIC:
                date = cell.getDateCellValue();
                break;
            default:
                throw new RuntimeException();
        }
        return date;
    }

    private static String getStringCellValue(Cell cell){
        String value = "";
        if(cell==null){
            return value;
        }
        switch (cell.getCellTypeEnum()){
            case BLANK:
                break;
            case STRING:
                if(StringUtils.isNotBlank(cell.getStringCellValue())){
                    value = cell.getStringCellValue().trim();
                }
                break;
            case NUMERIC:
                if (HSSFDateUtil.isCellDateFormatted(cell)) {
                    // 处理日期格式、时间格式
                    SimpleDateFormat sdf = null;
                    if (cell.getCellStyle().getDataFormat() == HSSFDataFormat.getBuiltinFormat("h:mm")) {
                        // 时间
                        sdf = new SimpleDateFormat("HH:mm");
                    } else {
                        // 日期
                        sdf = new SimpleDateFormat("yyyy-MM-dd");
                    }
                    Date date = cell.getDateCellValue();
                    value = sdf.format(date);
                } else if (cell.getCellStyle().getDataFormat() == 58) {
                    // 处理自定义日期格式：m月d日(通过判断单元格的格式id解决，id的值是58)
                    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
                    double valued = cell.getNumericCellValue();
                    Date date = DateUtil.getJavaDate(valued);
                    value = sdf.format(date);
                } else {
                    long longVal = Math.round(cell.getNumericCellValue());
                    if(Double.parseDouble(longVal + ".0") == cell.getNumericCellValue()) {
                        value = String.valueOf(longVal);
                    } else {
                        value = String.valueOf(Double.valueOf(cell.getNumericCellValue()));
                    }
                }
                break;
            default:
                break;
        }
        return value==null ? "" : value;
    }

    /**
     * 校验
     * @param fieldValue
     * @param excelField
     * @param rowErrors
     */
    private static void verify(Object fieldValue, ExcelField excelField, List<String> rowErrors) {
        String fieldName = excelField.getFieldName();
        if (excelField.isNotNull() && null == fieldValue){
            rowErrors.add(fieldName+"不能为空");
        }

        if (ExcelFieldType.String.toName().equals(excelField.getFieldType()) && null != fieldValue){
            String stringValue = (String) fieldValue;
            stringValue = stringValue.trim();
            if (stringValue.length() > excelField.getFieldLength()){
                rowErrors.add(fieldName+"最多"+excelField.getFieldLength()+"个字符");
            }
        }
    }

    private static void addErrorLogByReadTypeError(List<String> rowErrors, ExcelField excelField){
        ExcelFieldType excelFieldType = ExcelFieldType.fromName(excelField.getFieldType());
        if (null == excelFieldType){
            rowErrors.add(excelField.getFieldName() + "格式错误");
        }else {
            switch (excelFieldType) {
                case Integer:
                    rowErrors.add(excelField.getFieldName() + "必须是整数");
                    break;
                case Double:
                case Float:
                case Long:
                case Short:
                    rowErrors.add(excelField.getFieldName() + "必须是数字");
                    break;
                case Boolean:
                    rowErrors.add(excelField.getFieldName() + "格式错误");
                    break;
                case Date:
                    rowErrors.add(excelField.getFieldName() + "必须是日期");
                    break;
                default:
                    rowErrors.add(excelField.getFieldName() + "格式错误");
            }
        }
    }

    private static <T> void setRowNumToEntity(Class<T> resultType, T entity, Integer rowNum) throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {
        Method setRowNum = resultType.getMethod("setRowNum", Integer.class);
        if (null != setRowNum) {
            setRowNum.invoke(entity, rowNum);
        }
    }

    /**
     * 获取有效行数
     * @param sheet
     * @return
     */
    public static int getNum(Sheet sheet) {
        CellReference cellReference = new CellReference("A4");
        boolean flag = false;

        for (int i = cellReference.getRow(); i <= sheet.getLastRowNum();) {
            Row r = sheet.getRow(i);
            if(r == null){
                //如果是空行（即没有任何数据、格式），直接把它以下的数据往上移动
                sheet.shiftRows(i+1, sheet.getLastRowNum(),-1);
                continue;
            }
            flag = false;
            for(Cell c : r){
                if(c.getCellType() != Cell.CELL_TYPE_BLANK){
                    flag = true;
                    break;
                }
            }
            if(flag){
                i++;
                continue;
            }else{//如果是空白行（即可能没有数据，但是有一定格式）
                if(i == sheet.getLastRowNum()){
                    //如果到了最后一行，直接将那一行remove掉
                    sheet.removeRow(r);
                }else{
                    //如果还没到最后一行，则数据往上移一行
                    sheet.shiftRows(i+1, sheet.getLastRowNum(),-1);
                }
            }
        }

        return sheet.getLastRowNum() + 1;
    }
}
