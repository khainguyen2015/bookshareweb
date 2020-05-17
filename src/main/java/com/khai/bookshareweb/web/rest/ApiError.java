/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.khai.bookshareweb.web.rest;

import java.util.List;
import org.springframework.http.HttpStatus;

/**
 *
 * @author Acer
 */
public class ApiError {
    
    private HttpStatus httpStatus;
    private String message;
    private List<String> erros;

    public ApiError(HttpStatus httpStatus, String message, List<String> erros) {
        this.httpStatus = httpStatus;
        this.message = message;
        this.erros = erros;
    }

    public HttpStatus getHttpStatus() {
        return httpStatus;
    }

    public String getMessage() {
        return message;
    }

    public List<String> getErros() {
        return erros;
    }
    
    

}
