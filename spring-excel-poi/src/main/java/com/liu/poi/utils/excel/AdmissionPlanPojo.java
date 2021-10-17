package com.liu.poi.utils.excel;

/**
 * @author ly
 * @date 2019/8/14 9:33
 */
public class AdmissionPlanPojo extends BaseExcelImportTemplateProtocol {
    /**
     * 学校
     */
    @ExcelElement(fieldName = "学校",maxLength = 32,isNotNull = true)
    private String school;
    /**
     * 年级
     */
    @ExcelElement(fieldName = "年级",maxLength = 16,isNotNull = true)
    private String grade;
    /**
     * 学生
     */
    @ExcelElement(fieldName = "学生",maxLength = 16,isNotNull = true)
    private String student;
    /**
     * 性别
     */
    @ExcelElement(fieldName = "性别",maxLength = 6,isNotNull = true)
    private String sex;
    /**
     * 年龄
     */
    @ExcelElement(fieldName = "年龄",isNotNull = true)
    private Integer age;
    /**
     * 编号
     */
    @ExcelElement(fieldName = "编号",maxLength = 32,isNotNull = true)
    private String code;
    /**
     * 总分成绩
     */
    @ExcelElement(fieldName = "总分成绩",maxLength = 32,isNotNull = true)
    private Double totalFraction;

    public String getSchool() {
        return school;
    }

    public void setSchool(String school) {
        this.school = school;
    }

    public String getGrade() {
        return grade;
    }

    public void setGrade(String grade) {
        this.grade = grade;
    }

    public String getStudent() {
        return student;
    }

    public void setStudent(String student) {
        this.student = student;
    }

    public String getSex() {
        return sex;
    }

    public void setSex(String sex) {
        this.sex = sex;
    }

    public Integer getAge() {
        return age;
    }

    public void setAge(Integer age) {
        this.age = age;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public Double getTotalFraction() {
        return totalFraction;
    }

    public void setTotalFraction(Double totalFraction) {
        this.totalFraction = totalFraction;
    }

    public AdmissionPlanPojo() {

    }

    @Override
    public String toString() {
        return "AdmissionPlanPojo{" +
                "school='" + school + '\'' +
                ", grade='" + grade + '\'' +
                ", student='" + student + '\'' +
                ", sex='" + sex + '\'' +
                ", age=" + age +
                ", code='" + code + '\'' +
                ", totalFraction=" + totalFraction +
                '}';
    }
}
