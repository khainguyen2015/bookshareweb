/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.khai.bookshareweb.validation;

import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

/**
 *
 * @author Acer
 */
public class EmailValidator implements ConstraintValidator<ValidEmail, String>{
    
    private Pattern pattern;
    private Matcher matcher;
    private static final String EMAIL_PATTERN = "^[_A-Za-z0-9-+]+(.[_A-Za-z0-9-]+)*@[A-Za-z0-9-]+(.[A-Za-z0-9]+)*(.[A-Za-z]{2,})$";
    
    @Override
    public void initialize(ValidEmail validEmail) {
        
    }
    
    @Override
    public boolean isValid(String email, ConstraintValidatorContext context) {
        if(validateEmail(email)) {
            return true;
        }
        context.disableDefaultConstraintViolation();
        context.buildConstraintViolationWithTemplate("form.register.error.email.format").addConstraintViolation();
        return false;
    }
    
    private boolean validateEmail(String email) {
        return email.matches(EMAIL_PATTERN);
    }
    
}
