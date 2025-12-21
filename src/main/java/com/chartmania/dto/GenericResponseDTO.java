package com.chartmania.dto;

public class GenericResponseDTO  <T>{
    private boolean success;
    private String message;
    private T data;

    public GenericResponseDTO(boolean success, String message, T data) {
        this.success = success;
        this.message = message;
        this.data = data;
    }

    public GenericResponseDTO(boolean success, String message){
        this.success = success;
        this.message = message;
    }

    public boolean isSuccess(){
        return this.success;
    }

    public String getMessage(){
        return this.message;
    }

    public T getData(){
        return this.data;
    }
    
}
