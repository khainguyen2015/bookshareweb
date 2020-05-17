/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.khai.bookshareweb.data;

import com.khai.bookshareweb.entity.VerificationToken;

/**
 *
 * @author Acer
 */
public interface VerificationTokenRepository {
    
    public VerificationToken save(VerificationToken verificationToken);
    
    public VerificationToken findByToken(String token);
    
}
