/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.khai.bookshareweb.service;

import com.khai.bookshareweb.data.UserAccountRepository;
import com.khai.bookshareweb.entity.User;
import com.khai.bookshareweb.entity.UserAccount;
import java.util.ArrayList;
import java.util.List;
import javax.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

/**
 *
 * @author Acer
 */
@Service
public class SecurityServiceImpl implements SecurityService {
    
    @Autowired
    private UserAccountRepository userAccountRepository;
    
    
    public UserDetails buildUser(UserAccount userAccount) {
        boolean enable = true;
        boolean accountNonExpired = true;
        boolean credentialsNonExpired = true;
        boolean accountNonLocked = true;
        List<GrantedAuthority> authorities = new ArrayList<>();
        authorities.add(new SimpleGrantedAuthority("ROLE_USER"));
        UserDetails userDetail = new org.springframework.security.core.userdetails.User(userAccount.getAccountName(),
            userAccount.getPassword(), enable, accountNonExpired, credentialsNonExpired, accountNonLocked, authorities);
        return userDetail;
    }
    
    @Override
    public Authentication getAuthentication() {
        return SecurityContextHolder.getContext().getAuthentication();
    }
    
    @Override
    public User getCurrentLoggedUser() {
        Authentication authentication = getAuthentication();
        if(!isValidAuthentication(authentication)) {
            return null;
        }
        String userAccountName = authentication.getName();
        return userAccountRepository.findByUsername(userAccountName).getUser();
    }
    
    @Override
    public User getCurrentLoggedUserInSession(HttpSession session) {
        User user = (User)session.getAttribute("currentUser");
        if(user == null) {
            System.out.println("Store logged user info into session");
            user = getCurrentLoggedUser();
            if(user == null) {
                return null;
            }
            session.setAttribute("currentUser", user);
        }
        return user;
    }
    
    private boolean isValidAuthentication(Authentication authentication) {
        return authentication != null && !(authentication instanceof AnonymousAuthenticationToken);
    }
    
}
