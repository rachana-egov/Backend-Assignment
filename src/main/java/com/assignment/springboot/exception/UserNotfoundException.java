package com.assignment.springboot.exception;

public class UserNotfoundException extends RuntimeException{
    public UserNotfoundException(String message){
        super(message);
    }
}
