package com.liu.poi.utils.excel;

import org.apache.poi.ss.usermodel.*;
import org.apache.poi.ss.util.CellRangeAddress;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * @author ly
 * @date 2019/8/14 9:28
 */
public class ExcelTestServiceImpl {

    public static void main(String[] args) throws  Exception{
        String path = "E:\\wh-excel\\test\\excel_test.xlsx";
        final InputStream inputStream = new FileInputStream(new File(path));
        Workbook workbook = WorkbookFactory.create(inputStream);

        List<AdmissionPlanPojo> list = new ArrayList<>();
        Integer dataStartIndex = 1;
        //importExcel(workbook,list,dataStartIndex);



        //importExcel2(workbook,dataStartIndex);

        System.out.println();
        System.out.println();
        System.out.println();
        System.out.println();

        importExcel3(path);
    }


    public static void importExcel3(String path) throws Exception {
        //取得excel本地文件
        File importFile = new File(path);
        //从excel读取 商品 正面 清单 数据
        ExcelReadResult<AdmissionPlanPojo> resultVo = ExcelComplexReadUtil.read(importFile, AdmissionPlanPojo.class,null);
        List<AdmissionPlanPojo> result = resultVo.getResult();
        List<ExcelErrorLog> errorLogs = resultVo.getErrorLogs();


        for(AdmissionPlanPojo admissionPlanPojo : result){
            System.out.println(admissionPlanPojo.toString());
        }

        for(ExcelErrorLog excelErrorLog : errorLogs){
            System.out.println(excelErrorLog.toString());
        }
    }


    /**
     *
     * @param workbook
     * @param startReadLine 开始读取的行:从0开始
     */
    public static void importExcel2(Workbook workbook,Integer startReadLine) throws Exception{

        Sheet sheet = workbook.getSheetAt(0);//默认得到第一个工作部
        FormulaEvaluator evaluator = workbook.getCreationHelper().createFormulaEvaluator();

        Row filedNameRow = sheet.getRow(0);
        Map<Integer,String> titleMap = new HashMap<>();
        for (int columnIndex = 0; columnIndex < filedNameRow.getLastCellNum(); columnIndex++){
            Cell filedNameCell = filedNameRow.getCell(columnIndex);
            if (null == filedNameCell || filedNameCell.getStringCellValue().length() < 1){
                continue;
            }
            String filedName = filedNameCell.getStringCellValue().trim();
            titleMap.put(columnIndex,filedName);
        }


        //获取表格字段信息
        List<ExcelField> excelFields =  ExcelBaseUtil.getExcelFields(sheet, AdmissionPlanPojo.class,startReadLine - 1 );
        Map<Integer,ExcelField> map = excelFields.stream().collect(Collectors.toMap(ExcelField::getColumnIndex, Function.identity()));

        List<AdmissionPlanPojo> results = new ArrayList<>();
        Row row = null;
        int physicalNumberOfRows = sheet.getPhysicalNumberOfRows();
        for(int r = startReadLine; r < physicalNumberOfRows; r++) {//遍历当前行
            row = sheet.getRow(r);
            if(row == null){
                continue;
            }

            AdmissionPlanPojo data = new AdmissionPlanPojo();
            for(Cell cell : row) {
                int columnIndex = cell.getColumnIndex();
                ExcelField excelField = map.get(columnIndex);

                ExcelFieldType excelFieldType = ExcelFieldType.fromName(excelField.getFieldType());
                Method method = excelField.getMethod();
                boolean isMerge = ExcelAnalysisUtil.isMergedRegion(sheet, r, columnIndex);//合并的单元格
                Object fieldValue = null;
                if(isMerge){
                    fieldValue = ExcelAnalysisUtil.getMergedRegionValue(sheet, row.getRowNum(), columnIndex,excelFieldType,evaluator);
                }else{
                    fieldValue = ExcelAnalysisUtil.getCellValue(cell,excelFieldType,evaluator);
                }
                if (null != fieldValue && null != method && null != data){
                    method.invoke(data, fieldValue);
                }
            }
            results.add(data);
        }

        for(AdmissionPlanPojo admissionPlanPojo : results){
            System.out.println(admissionPlanPojo.toString());
        }
     }


    /**
     *
     * @param workbook
     * @param list
     * @param dataStartIndex 数据读取 开始索引
     */
    public static void importExcel(Workbook workbook,List<AdmissionPlanPojo> list,Integer dataStartIndex){
        final Sheet xssSheet0 = workbook.getSheetAt(0);//默认得到第一个工作部
        Row row;

        //合并单元格处理,获取合并行
        List<CellRangeAddress> cras = ExcelAnalysisUtil.getCombineCell(xssSheet0);
        //getPhysicalNumberOfRows() 获取的是物理行数，也就是不包括那些空行（隔行）的情况。
        int physicalNumberOfRows = xssSheet0.getPhysicalNumberOfRows();
        for (int i = dataStartIndex; i < physicalNumberOfRows; i++) {
            Row BigRow = xssSheet0.getRow(i);
            if (BigRow == null) {
                break;
            }

            System.out.println("===========外部循环 i = " + i);

            //判断该行 指定的单元格是否是合并单元格
            boolean mergedRegion = ExcelAnalysisUtil.isMergedRegion(xssSheet0, i, 0);//这里不能改变 固定 检查 每一行的第一列

            if (mergedRegion) {
                int lastRow = ExcelAnalysisUtil.getRowNum(cras, xssSheet0.getRow(i).getCell(0), xssSheet0);
                for (; i <= lastRow; i++) {
                    System.out.println("当前行 》 i = " + i);

                    row = xssSheet0.getRow(i);

                    // 判断该行第3列是合并单元格
                    if (ExcelAnalysisUtil.isMergedRegion(xssSheet0, i, 2)) {
                        int lastRow2 = ExcelAnalysisUtil.getRowNum(cras, xssSheet0.getRow(i).getCell(2), xssSheet0);
                        for (; i <= lastRow2; i++) {
                            Row nextRow = xssSheet0.getRow(i);

                            AdmissionPlanPojo admissionPlan = buildAdmission(row, BigRow, nextRow);
                            saveAdmissionPlanPojo(admissionPlan);
                            list.add(admissionPlan);
                        }
                    } else {

                        AdmissionPlanPojo admissionPlan = buildAdmission(row, BigRow, row);
                        saveAdmissionPlanPojo(admissionPlan);
                        list.add(admissionPlan);
                    }

                }
                i--; //关键
            } else {

                row = xssSheet0.getRow(i);
                AdmissionPlanPojo admissionPlan = buildAdmission(row, BigRow, row);

                saveAdmissionPlanPojo(admissionPlan);
                list.add(admissionPlan);
            }

        }
    }

    private static void saveAdmissionPlanPojo(AdmissionPlanPojo admissionPlan){
        System.out.println("保存成功 - "+admissionPlan.toString());
    }

    private static AdmissionPlanPojo buildAdmission(Row row, Row BigRow, Row nextRow) {
        AdmissionPlanPojo admissionPlan = new AdmissionPlanPojo();
       /* final String school = ExcelAnalysisUtil.getCellValue(row.getCell(0)); // 学校
        admissionPlan.setSchool(school);

        String grade = ExcelAnalysisUtil.getCellValue(row.getCell(1)); // 年级
        admissionPlan.setGrade(grade);

        String student = ExcelAnalysisUtil.getCellValue(row.getCell(2)); // 学生
        admissionPlan.setStudent(student);

        String sex = ExcelAnalysisUtil.getCellValue(row.getCell(3)); // 性别
        admissionPlan.setSex(sex);


        String cellValue = ExcelAnalysisUtil.getCellValue(row.getCell(4)); //年龄



        String code = ExcelAnalysisUtil.getCellValue(row.getCell(5));//编号
        admissionPlan.setCode(code);*/

        return admissionPlan;
    }

}
