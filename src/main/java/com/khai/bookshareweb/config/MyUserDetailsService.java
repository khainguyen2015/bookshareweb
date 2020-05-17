/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.khai.bookshareweb.config;

import com.khai.bookshareweb.data.UserAccountRepository;
import com.khai.bookshareweb.entity.UserAccount;
import com.khai.bookshareweb.service.LoginAttemptService;
import com.khai.bookshareweb.utils.RequestUtils;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import javax.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;


/**
 *
 * @author Acer
 */
@Component
public class MyUserDetailsService implements UserDetailsService {
    
    private static final int ROLE_ADMIN_ID = 1;
    
    private static final int ROLE_USER_ID = 2;
    
    
    private final UserAccountRepository userAccountRepository;
    
    @Autowired
    private LoginAttemptService loginAttemptService;
    
    @Autowired
    private HttpServletRequest request;
    
    public MyUserDetailsService(UserAccountRepository userAccountRepository) {
        this.userAccountRepository = userAccountRepository;
    }
    
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        
        boolean isCurrentIpBlocked = loginAttemptService.isBlocked(RequestUtils.getIpFromRequest(request));
        
        System.out.println(Thread.currentThread().getName());
        
        if(isCurrentIpBlocked) {
            throw new RuntimeException("blocked_ip");
        }
        
        UserAccount userAccount = userAccountRepository.findByUsername(username);
        if(userAccount != null) {
            List<GrantedAuthority> authorities = new ArrayList<>();
            if(userAccount.getRoleId() == ROLE_ADMIN_ID) {
                authorities.add(new SimpleGrantedAuthority("ROLE_ADMIN"));
            }
            if(userAccount.getRoleId() == ROLE_USER_ID) {
                authorities.add(new SimpleGrantedAuthority("ROLE_USER"));
            }
            String password = userAccount.getPassword();
            System.out.println("Encoded password: " + password);
            boolean enabled = userAccount.isEnable();
            boolean accountNonExpired = true;
            boolean credentialsNonExpired = true;
            boolean accountNonLocked = true;
            return new User(
                userAccount.getAccountName(),
                password,
                enabled, 
                accountNonExpired, 
                credentialsNonExpired, 
                accountNonLocked, 
                authorities);
        }
        throw new UsernameNotFoundException("User '" + username + "' not found.");
    }
}
