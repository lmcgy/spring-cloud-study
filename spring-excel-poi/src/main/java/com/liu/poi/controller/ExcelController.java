package com.liu.poi.controller;

import com.liu.poi.template.ExportTemplate;
import com.liu.poi.utils.excel.ExcelReadResult;
import com.liu.poi.utils.excel.ExcelReadUtil;
import com.liu.poi.utils.excel.ExcelWriteUtil;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;
import java.text.SimpleDateFormat;
import java.util.*;

@RestController
@RequestMapping("/excel")
public class ExcelController {

    @GetMapping("/export")
    public void export(HttpServletResponse response) throws Exception {

        List<Map<String,Object>> list = new ArrayList<>();

        Map<String,Object> map = new LinkedHashMap<>();
        map.put("id",1);
        map.put("name","刘苗");
        String strDateFormat = "yyyy-MM-dd HH:mm:ss";
        SimpleDateFormat sdf = new SimpleDateFormat(strDateFormat);
        String format = sdf.format(new Date());
        Date parse = sdf.parse(format);
        map.put("time",parse);
        map.put("money",2343D);
        list.add(map);


        ExcelWriteUtil.writeForOut(ExportTemplate.class, list ,response);

    }

    @PostMapping("/inPort")
    public String inPort(@RequestParam("file") MultipartFile file) throws Exception {

        if (file.isEmpty()) {
            return "上传失败，请选择文件";
        }

        ExcelReadResult<ExportTemplate> read = ExcelReadUtil.read(file.getInputStream(), ExportTemplate.class, 0, 1, -1);
        read.getResult().forEach(System.out::println);
        return "ok";

    }

}
