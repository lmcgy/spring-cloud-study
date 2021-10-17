package com.liu.mvc.exception;


import com.liu.mvc.result.CodeMsg;
import com.liu.mvc.result.Result;
import org.springframework.validation.BindException;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.List;

@ControllerAdvice
@ResponseBody
public class GlobExceptionHandler {

    @ExceptionHandler(value = Exception.class)
    public Result<Object> handle(Exception e){
        if (e instanceof GlobException){
            GlobException ex = (GlobException)e;
            return Result.error(ex.getCm());
        }else if (e instanceof BindException){
            BindException ex = (BindException)e;
            List<ObjectError> errors = ex.getAllErrors();
            ObjectError error = errors.get(0);
            String msg = error.getDefaultMessage();
            return Result.error(CodeMsg.BIND_ERROR.fullMsg(msg));
        }else {
            return Result.error(CodeMsg.SERVER_ERROR);
        }

    }


}
