package com.sky.exception;

public class UsernameAlreadyExistsException extends BaseException{
    public UsernameAlreadyExistsException(){
    }

    public UsernameAlreadyExistsException(String msg){
        super(msg);
    }
}
