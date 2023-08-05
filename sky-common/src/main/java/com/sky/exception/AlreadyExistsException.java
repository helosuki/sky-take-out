package com.sky.exception;
/*
* 唯一字段重复异常
* */
public class AlreadyExistsException extends BaseException{
    public AlreadyExistsException(){
    }

    public AlreadyExistsException(String msg){
        super(msg);
    }
}
