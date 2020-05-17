/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.khai.bookshareweb.data;

import com.khai.bookshareweb.entity.UserAccount;

/**
 *
 * @author Acer
 */
public interface UserAccountRepository {
    
    public UserAccount findByUsername(String username);
    
    public UserAccount save(UserAccount userAccount);
    
    public UserAccount update(UserAccount userAccount);
    
}
