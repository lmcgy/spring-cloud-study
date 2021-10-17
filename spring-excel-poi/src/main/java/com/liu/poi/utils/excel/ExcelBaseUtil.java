package com.liu.poi.utils.excel;

import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.poi.ss.formula.functions.T;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Type;
import java.util.*;

public class ExcelBaseUtil {
    private static final Logger logger = LogManager.getLogger(ExcelBaseUtil.class);

    /**
     *  获取excel 字段
     * @param sheet 单元表
     * @param resultType 类型
     * @param titleRowNum 标题行 行号 （数据读取行/数据填写行） - 1
     * @return
     * @throws Exception
     */
    public static List<ExcelField> getExcelFields(Sheet sheet, Class resultType, int titleRowNum) throws Exception{
        return getExcelFieldsByClazz(sheet,resultType,titleRowNum);
    }

    /**
     * 获取excel 字段
     * @param sheet 单元表
     * @param resultType 类型
     * @param filedNameRowIndex 字段名称 行号
     * @return
     * @throws Exception
     */
    public static List<ExcelField> getExcelFieldsByClazz(Sheet sheet, Class resultType, Integer filedNameRowIndex)throws Exception{
        Row filedNameRow = sheet.getRow(filedNameRowIndex);
        List<ExcelField> excelFieldList = null;

        if(filedNameRow != null){ //导入
            //注意：getLastCellNum返回的是cell的个数，不是最后一个cell的下标
            int cellCount = filedNameRow.getLastCellNum();
            excelFieldList = new ArrayList<>(cellCount);

            Map<String, ExcelField> excelFieldMap = getExcelFieldMap(resultType);
            for (int columnIndex = 0; columnIndex < cellCount; columnIndex++){
                Cell filedNameCell = filedNameRow.getCell(columnIndex);
                if (null == filedNameCell || filedNameCell.getStringCellValue().length() < 1){
                    continue;
                }
                String filedName = filedNameCell.getStringCellValue().trim();
                ExcelField excelField = excelFieldMap.get(filedName);
                if(excelField == null){
                    logger.error("ExcelBaseUtil》getExcelFields》Excel标题名称校验为空》filedName = " + filedName);
                }
                excelField.setColumnIndex(columnIndex);
                int columnWidth = sheet.getColumnWidth(columnIndex);
                excelField.setColumnWidth(columnWidth);
                excelFieldMap.put(filedName,excelField);
            }
            excelFieldList.addAll(excelFieldMap.values());
            Collections.sort(excelFieldList, new Comparator<ExcelField>(){
                public int compare(ExcelField e1, ExcelField e2) {
                    //按照ExcelField的索引进行升序排列
                    if(e1.getColumnIndex() > e2.getColumnIndex()){
                        return 1;
                    }
                    if(e1.getColumnIndex() == e2.getColumnIndex()){
                        return 0;
                    }
                    return -1;
                }
            });
        }else{  //导出
            Map<String, ExcelField> excelFieldMap = getExcelFieldMap(resultType);
            excelFieldList = new ArrayList<>(excelFieldMap.size());
            excelFieldList.addAll(excelFieldMap.values());
            for(int i = 0 ; i < excelFieldList.size() ; i++){
                ExcelField excelField = excelFieldList.get(i);
                excelField.setColumnIndex(i);
            }
        }
        return excelFieldList;
    }


    /**
     * 获取Excel字段对象
     * @param resultType
     * @return
     * @throws Exception
     */
    private static Map<String,ExcelField> getExcelFieldMap(Class<T> resultType)throws Exception{
        String prefix = "ExcelBaseUtil》getExcelFieldMapExcelFields》";
        Map<String,ExcelField> filedMap = new HashMap<>();
        Field[] fs =  resultType.getDeclaredFields ();
        Method[] methods = resultType.getDeclaredMethods();

        for ( int i = 0 ; i < fs.length ; i++){
            Field field = fs[i];
            String name = field.getName();
            Field declaredField = resultType.getDeclaredField(name);
            ExcelElement excelElement = declaredField.getAnnotation(ExcelElement.class);
            if(excelElement == null){
                continue;
            }
            ExcelField excelField = new ExcelField();
            //注解上的值
            String fieldName = excelElement.fieldName();
            if(StringUtils.isEmpty(fieldName)){
                logger.error(prefix + "Excel字段名为空");
            }
            excelField.setFieldName(fieldName);//字段名称
            excelField.setFieldCode(name);//字段编码
            Type genericType = field.getGenericType();
            String typeName = genericType.getTypeName();
            excelField.setFieldType(typeName.substring(typeName.lastIndexOf(".") + 1 ));//字段类型
            excelField.setFieldLength(excelElement.maxLength());//字段长度
            excelField.setNotNull(excelElement.isNotNull());//是否可以为空
            excelField.setColumnWidth(fieldName.length() + 10);

            String setMethodNm = "set" + name.substring(0, 1).toUpperCase() + name.substring(1);
            for (Method method : methods) {
                if (setMethodNm.equals(method.getName())){
                    excelField.setMethod(method);
                    break;
                }
            }
            filedMap.put(fieldName,excelField);
        }
        return filedMap;
    }

    /**
     * 清除空行
     */
    public static void clearBlankRow(Sheet sheet){
        int i = sheet.getLastRowNum();
        Row tempRow;
        while(i > 0){
            i--;
            tempRow = sheet.getRow(i);
            if(tempRow == null){
                sheet.shiftRows(i+1, sheet.getLastRowNum(), -1);
            }
        }

    }

    /**
     * 拷贝样式
     * @param toStyle 目标样式
     * @param fromStyle 源样式
     */
    public static void copyCellStyle(CellStyle toStyle, CellStyle fromStyle) {
        if(fromStyle == null){
            return;
        }
        //对齐方式
        toStyle.setAlignment(fromStyle.getAlignmentEnum());
        toStyle.setVerticalAlignment(fromStyle.getVerticalAlignmentEnum());

        //边框和边框颜色
        toStyle.setBorderBottom(fromStyle.getBorderBottomEnum());
        toStyle.setBorderLeft(fromStyle.getBorderLeftEnum());
        toStyle.setBorderRight(fromStyle.getBorderRightEnum());
        toStyle.setBorderTop(fromStyle.getBorderTopEnum());
        toStyle.setTopBorderColor(fromStyle.getTopBorderColor());
        toStyle.setBottomBorderColor(fromStyle.getBottomBorderColor());
        toStyle.setRightBorderColor(fromStyle.getRightBorderColor());
        toStyle.setLeftBorderColor(fromStyle.getLeftBorderColor());

        //背景和前景
        toStyle.setFillBackgroundColor(fromStyle.getFillBackgroundColor());
        toStyle.setFillForegroundColor(fromStyle.getFillForegroundColor());

        //首行缩进
        toStyle.setIndention(fromStyle.getIndention());
        toStyle.setLocked(fromStyle.getLocked());
        //旋转
        toStyle.setRotation(fromStyle.getRotation());
        toStyle.setWrapText(fromStyle.getWrapText());

        toStyle.setDataFormat(fromStyle.getDataFormat());
        toStyle.setHidden(fromStyle.getHidden());
    }
}
