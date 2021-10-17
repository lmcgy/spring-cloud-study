package com.liu.mvc.controller;


import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/handle")
public class HandleController {


    /**
     * controller内部异常处理
     * @param e
     * @return
     */
    /*@ExceptionHandler(value = Exception.class)
    public String handle(Exception e){
        return "error";
    }*/



    @GetMapping("/get")
    @ResponseBody
    public String showHandle(@RequestParam Integer num){
        if (10/num==1){
            return "success";
        }
        return "failed";
    }

}
