package com.medhir.rest.exception;

public class PermissionDeniedException extends RuntimeException{
    public PermissionDeniedException(String message){
        super(message);
    }
}