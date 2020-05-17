/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.khai.bookshareweb.dto;

import com.khai.bookshareweb.validation.PasswordMatches;
import com.khai.bookshareweb.validation.ValidEmail;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;


/**
 *
 * @author Acer
 */
@PasswordMatches(message="form.register.error.passwordConfirm.notMatch")
public class UserDto {
    
    @NotNull
    @Size(min=5, max=50, message="form.register.error.userName.size")
    @Pattern(regexp="[A-Za-z0-9]{5,50}", message="form.register.error.userName.format")
    private String userName;
    
    @NotNull
    @ValidEmail(message="form.register.error.userName.size")
    private String email;
    
    @NotNull
    @Size(min=5, max=50, message="form.register.error.accountName.size")
    @Pattern(regexp="[A-Za-z0-9]{5,50}", message="form.register.error.accountName.format")
    private String accountName;
    
    @NotNull
    @Size(min=5, max=50, message="form.register.error.password.size")
    private String password;
    
    @NotNull
    @Size(min=5, max=50, message="form.register.error.passwordConfirm.size")
    private String passwordConfirm;
    

    private String picture = null;

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

    public String getAccountName() {
        return accountName;
    }

    public void setAccountName(String accountName) {
        this.accountName = accountName;
    }

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

    public String getPicture() {
        return picture;
    }

    public void setPicture(String picture) {
        this.picture = picture;
    }
    
    
}
