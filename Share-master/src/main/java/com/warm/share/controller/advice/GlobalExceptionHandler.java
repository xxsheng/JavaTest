package com.warm.share.controller.advice;

import com.warm.share.exception.BusinessException;
import com.warm.share.model.BaseModel;

import org.springframework.validation.BindException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;

@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(Exception.class)
    @ResponseBody
    public BaseModel handleException(Exception e) {
        return BaseModel.fail(e.getMessage());
    }

    @ExceptionHandler(BusinessException.class)
    @ResponseBody
    public BaseModel handleBusinessException(BusinessException e) {
        return BaseModel.fail(e.getCode(), e.getMessage());
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    @ResponseBody
    public BaseModel handelM(MethodArgumentNotValidException e) {

        return BaseModel.fail(e.getBindingResult().getAllErrors().get(0).getDefaultMessage());
    }

    @ExceptionHandler(BindException.class)
    @ResponseBody
    public BaseModel handelBindException(BindException e) {
        System.out.println(e.getBindingResult().getAllErrors().get(0).getDefaultMessage());
        //System.out.println(e.getBindingResult().getAllErrors().get(1).getDefaultMessage());
        System.out.println("dsad");
        return BaseModel.fail(e.getBindingResult().getAllErrors().get(0).getDefaultMessage());
    }


}
