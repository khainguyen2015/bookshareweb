/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.khai.bookshareweb.dto;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;

/**
 *
 * @author Acer
 */
public class PasswordDTO {
    @NotNull
    @Size(min=5, max=50, message="form.register.error.password.size")
    @Pattern(regexp="[A-Za-z0-9]{5,50}", message="form.register.error.password.format")
    private String password;
    
    @NotNull
    @Size(min=5, max=50, message="form.register.error.passwordConfirm.size")
    @Pattern(regexp="[A-Za-z0-9]{5,50}", message="form.register.error.passwordConfirm.format")
    private String passwordConfirm;

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getPasswordConfirm() {
        return passwordConfirm;
    }

    public void setPasswordConfirm(String passwordConfirm) {
        this.passwordConfirm = passwordConfirm;
    }
    
    
}
