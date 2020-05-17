/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.khai.bookshareweb.service.exception;

/**
 *
 * @author Acer
 */
public class OldPasswordNotMatchesException extends RuntimeException {
    
    public OldPasswordNotMatchesException() {
        
    }
    
    public OldPasswordNotMatchesException(String message) {
        super(message);
    }
    
}
