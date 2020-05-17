/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.khai.bookshareweb.data;

import com.khai.bookshareweb.entity.PasswordResetToken;

/**
 *
 * @author Acer
 */
public interface PasswordResetTokenRepository {
    public PasswordResetToken save(PasswordResetToken passwordResetToken);
    
    public PasswordResetToken remove(PasswordResetToken passwordResetToken);
    
    public PasswordResetToken findByToken(String token);
}
