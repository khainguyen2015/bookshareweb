/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.khai.bookshareweb.dto;

import com.khai.bookshareweb.validation.ValidEmail;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;

/**
 *
 * @author Acer
 */
public class UserProfileDto {
    
    @NotNull
    @Size(min=5, max=50, message="form.register.error.userName.size")
    @Pattern(regexp="[A-Za-z0-9]{5,50}", message="form.register.error.userName.format")
    private String userName;
    
    @NotNull
    @ValidEmail(message="form.register.error.userName.size")
    private String email;

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }


    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }
    
}
