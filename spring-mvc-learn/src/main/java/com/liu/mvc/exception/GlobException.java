package com.liu.mvc.exception;

import com.liu.mvc.result.CodeMsg;

public class GlobException extends RuntimeException {

    private CodeMsg codeMsg;

    public GlobException(CodeMsg cm){
        super(cm.getMsg());
        this.codeMsg = cm;
    }

    public CodeMsg getCm(){
        return codeMsg;
    }

}
