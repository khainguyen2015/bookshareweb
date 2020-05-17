/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.khai.bookshareweb.validation;

import com.khai.bookshareweb.dto.UserDto;
import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

/**
 *
 * @author Acer
 */
public class PasswordMatchesValidator implements ConstraintValidator<PasswordMatches, Object> {
    
    @Override
    public void initialize(PasswordMatches constraintAnnotation) {
        
    }
    
    @Override
    public boolean isValid(Object obj, ConstraintValidatorContext context) {
        UserDto user = (UserDto)obj;
        return user.getPassword().equals(user.getPasswordConfirm());
    }
        
    
}
