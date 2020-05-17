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
public class UserIdNotMatchesException extends RuntimeException {
    
    private static final String DEFAULT_MESSAGE = "User id not matches";
    
    public UserIdNotMatchesException() {
        super(DEFAULT_MESSAGE);
    }
    
    public UserIdNotMatchesException(String message) {
        super(message);
    }
    
}
