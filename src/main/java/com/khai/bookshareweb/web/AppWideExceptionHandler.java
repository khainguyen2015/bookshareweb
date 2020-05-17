/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.khai.bookshareweb.web;

import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.servlet.NoHandlerFoundException;

/**
 *
 * @author Acer
 */
@ControllerAdvice
public class AppWideExceptionHandler {
    
    @ExceptionHandler(NoHandlerFoundException.class)
    public String pageNotFoundHandler() {
        System.out.println("NotFound");
        return "/";
    }
    
}
