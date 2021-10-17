package com.liu.poi.utils.excel;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
public @interface ExcelElement {

    /**
     * 字段名
     * @return
     */
    String fieldName();

    /**
     * 最小长度
     * @return
     */
    int minLength() default 0;

    /**
     * 最大长度
     * @return
     */
    int maxLength() default 2147483647;

    /**
     * 是否不能为空
     * @return
     */
    boolean isNotNull()default false;

    /**
     * 正则
     * @return
     */
    String reg()default "";

}
