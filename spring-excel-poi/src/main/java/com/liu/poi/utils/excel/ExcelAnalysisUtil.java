package com.liu.poi.utils.excel;

import org.apache.commons.lang3.StringUtils;
import org.apache.poi.hssf.usermodel.HSSFDataFormat;
import org.apache.poi.hssf.usermodel.HSSFDateUtil;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.ss.util.CellRangeAddress;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

/**
 * 功能: poi导出excel 解析工具类
 * @author ly
 * @date 2019/8/16 17:17
 */
public class ExcelAnalysisUtil {


    /**
     * 保存行号信息给实体对象
     * @param resultType
     * @param entity 指定类型实体对象
     * @param rowNum 当前行号
     * @param <T>
     */
    public static <T> void setRowNumToEntity(Class<T> resultType, T entity, Integer rowNum) throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {
        Method setRowNum = resultType.getMethod("setRowNum", Integer.class);
        if (null != setRowNum) {
            setRowNum.invoke(entity, rowNum);
        }
    }

    /**
     * 根据读取字段类型 添加错误日志
     * @param rowErrors
     * @param excelField
     */
    public static void addErrorLogByReadTypeError(List<String> rowErrors, ExcelField excelField){
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

    /**
     * 合并单元格处理,获取合并行
     * @param sheet
     * @return List<CellRangeAddress>
     */
    public static List<CellRangeAddress> getCombineCell(Sheet sheet) {
        List<CellRangeAddress> list = new ArrayList<CellRangeAddress>();
        // 获得一个 sheet 中合并单元格的数量
        int sheetmergerCount = sheet.getNumMergedRegions();
        // 遍历所有的合并单元格
        for (int i = 0; i < sheetmergerCount; i++) {
            // 获得合并单元格保存进list中
            CellRangeAddress ca = sheet.getMergedRegion(i);
            list.add(ca);
        }
        return list;
    }


    /**
     * 判断单元格是否为合并单元格，是的话则将单元格的值返回
     * @param listCombineCell 存放合并单元格的list
     * @param cell 需要判断的单元格
     * @param sheet sheet
     * @return
     */
    public static Object isCombineCell(List<CellRangeAddress> listCombineCell, Cell cell, Sheet sheet) throws Exception {
        int firstC = 0;
        int lastC = 0;
        int firstR = 0;
        int lastR = 0;
        Object cellValue = null;
        for (CellRangeAddress ca : listCombineCell) {
            // 获得合并单元格的起始行, 结束行, 起始列, 结束列
            firstC = ca.getFirstColumn();
            lastC = ca.getLastColumn();
            firstR = ca.getFirstRow();
            lastR = ca.getLastRow();
            if (cell.getRowIndex() >= firstR && cell.getRowIndex() <= lastR) {
                if (cell.getColumnIndex() >= firstC && cell.getColumnIndex() <= lastC) {
                    Row fRow = sheet.getRow(firstR);
                    Cell fCell = fRow.getCell(firstC);
                    cellValue = getCellValue(fCell);
                    break;
                }
            } else {
                cellValue = "";
            }
        }
        return cellValue;
    }


    /**
     * 获取合并单元格的值
     *
     * @param sheet
     * @param row
     * @param column
     * @return
     */
    public static Object getMergedRegionValue(Sheet sheet, int row, int column,ExcelFieldType excelFieldType, FormulaEvaluator formulaEvaluator) {
        int sheetMergeCount = sheet.getNumMergedRegions();
        for (int i = 0; i < sheetMergeCount; i++) {
            CellRangeAddress ca = sheet.getMergedRegion(i);
            int firstColumn = ca.getFirstColumn();//起始列号
            int lastColumn = ca.getLastColumn();//终止列号
            int firstRow = ca.getFirstRow();//起始行号
            int lastRow = ca.getLastRow();//终止行号

            if (row >= firstRow && row <= lastRow) {
                if (column >= firstColumn && column <= lastColumn) {
                    Row fRow = sheet.getRow(firstRow);
                    Cell fCell = fRow.getCell(firstColumn);
                    return getCellValue(fCell,excelFieldType,formulaEvaluator);
                }
            }
        }
        return null;
    }


    /**
     * 判断指定的单元格是否是合并单元格
     * @param sheet
     * @param row 行下标
     * @param column 列下标
     * @return
     */
    public static boolean isMergedRegion(Sheet sheet, int row, int column) {
        int sheetMergeCount = sheet.getNumMergedRegions();
        for (int i = 0; i < sheetMergeCount; i++) {
            CellRangeAddress range = sheet.getMergedRegion(i);
            int firstColumn = range.getFirstColumn();
            int lastColumn = range.getLastColumn();
            int firstRow = range.getFirstRow();
            int lastRow = range.getLastRow();
            if (row >= firstRow && row <= lastRow) {
                if (column >= firstColumn && column <= lastColumn) {
                    return true;
                }
            }
        }
        return false;
    }


    /**
     * 获取行数
     * @param listCombineCell
     * @param cell
     * @param sheet
     * @return
     */
    public static int getRowNum(List<CellRangeAddress> listCombineCell, Cell cell, Sheet sheet) {
        int xr = 0;
        int firstC = 0;
        int lastC = 0;
        int firstR = 0;
        int lastR = 0;
        for (CellRangeAddress ca : listCombineCell) {
            // 获得合并单元格的起始行, 结束行, 起始列, 结束列
            firstC = ca.getFirstColumn();//起始列
            lastC = ca.getLastColumn();//结束列
            firstR = ca.getFirstRow();//获得合并单元格的起始行
            lastR = ca.getLastRow();//结束行
            if (cell.getRowIndex() >= firstR && cell.getRowIndex() <= lastR) {
                if (cell.getColumnIndex() >= firstC && cell.getColumnIndex() <= lastC) {
                    xr = lastR;
                }
            }
        }
        return xr;

    }

    /**
     * 获取有效行数
     * @param sheet
     * @return
     */
    public static int getNum(Sheet sheet) {
        org.apache.poi.hssf.util.CellReference cellReference = new org.apache.poi.hssf.util.CellReference("A4");
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


    /**
     * 获取单元格的值
     *
     * @param fCell
     * @return
     */
    public static Object getCellValue(Cell fCell) {
        if (fCell == null)
            return null;
        if (fCell.getCellType() == Cell.CELL_TYPE_STRING) {
            return fCell.getStringCellValue();
        } else if (fCell.getCellType() == Cell.CELL_TYPE_BOOLEAN) {
            return fCell.getBooleanCellValue();
        } else if (fCell.getCellType() == Cell.CELL_TYPE_FORMULA) {
            return fCell.getCellFormula();
        } else if (fCell.getCellType() == Cell.CELL_TYPE_NUMERIC) {
            return fCell.getNumericCellValue();
        }
        return null;
    }


    /**
     * 获取单元格的值
     * @param cell
     * @param excelFieldType
     * @param formulaEvaluator
     * @return
     */
    public static Object getCellValue(Cell cell, ExcelFieldType excelFieldType, FormulaEvaluator formulaEvaluator){
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


//----------------------------------------------------------------私有方法--------------分隔线-------------------

    /**
     * 获取单元格 【字符串】 类型数据
     * @param cell
     * @return
     */
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
     * 获取单元格 【数值】 类型数据
     * @param cell
     * @param formulaEvaluator
     * @return
     */
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
     * 获取单元格【日期】类型 数据
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

    /**
     * 获取单元格 【Boolean】 类型 数据
     */
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
}
