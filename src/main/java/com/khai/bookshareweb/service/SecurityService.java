/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.khai.bookshareweb.service;

import com.khai.bookshareweb.entity.User;
import com.khai.bookshareweb.entity.UserAccount;
import javax.servlet.http.HttpSession;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;

/**
 *
 * @author Acer
 */
public interface SecurityService {
    
    public UserDetails buildUser(UserAccount userAccount);
    
    public Authentication getAuthentication();
    
    public User getCurrentLoggedUser();
    
    public User getCurrentLoggedUserInSession(HttpSession session);
    
    
}
