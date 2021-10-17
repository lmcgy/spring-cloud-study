package com.liu.product.controller;


import com.liu.product.TestVo;
import org.springframework.web.bind.annotation.*;

@RestController("/test")
public class TestController {


    @PostMapping("/export")
    public Object export(@RequestBody TestVo testVo){
        System.out.println(testVo);
        return "success";
    }

    @GetMapping("/find")
    public Object find(@RequestParam String id){
        System.out.println(id);
        return "success";
    }
}
