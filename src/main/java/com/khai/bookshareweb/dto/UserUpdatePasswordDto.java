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
public class UserUpdatePasswordDto {
    
    @NotNull
    @Size(min=5, max=50, message="form.register.error.password.size")
    @Pattern(regexp="[A-Za-z0-9]{5,50}", message="form.register.error.password.format")
    private String currentPassword;
    
    @NotNull
    @Size(min=5, max=50, message="form.register.error.password.size")
    @Pattern(regexp="[A-Za-z0-9]{5,50}", message="form.register.error.password.format")
    private String newPassword;
    
    @NotNull
    @Size(min=5, max=50, message="form.register.error.passwordConfirm.size")
    @Pattern(regexp="[A-Za-z0-9]{5,50}", message="form.register.error.passwordConfirm.format")
    private String newPasswordConfirm;

    public String getCurrentPassword() {
        return currentPassword;
    }

    public void setCurrentPassword(String currentPassword) {
        this.currentPassword = currentPassword;
    }

    public String getNewPassword() {
        return newPassword;
    }

    public void setNewPassword(String newPassword) {
        this.newPassword = newPassword;
    }

    public String getNewPasswordConfirm() {
        return newPasswordConfirm;
    }

    public void setNewPasswordConfirm(String newPasswordConfirm) {
        this.newPasswordConfirm = newPasswordConfirm;
    }

    
    
}
