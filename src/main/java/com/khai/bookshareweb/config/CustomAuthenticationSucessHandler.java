/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.khai.bookshareweb.config;

import com.khai.bookshareweb.service.LoginAttemptService;
import com.khai.bookshareweb.utils.RequestUtils;
import java.io.IOException;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.SavedRequestAwareAuthenticationSuccessHandler;

/**
 *
 * @author Acer
 */
public class CustomAuthenticationSucessHandler extends SavedRequestAwareAuthenticationSuccessHandler{
    
    @Autowired
    private LoginAttemptService loginAttemptService;
    
    
    
    @Override
    public void onAuthenticationSuccess(HttpServletRequest request,
			HttpServletResponse response, Authentication authentication)
			throws IOException, ServletException {
        loginAttemptService.loginSuccess(RequestUtils.getIpFromRequest(request));
        super.onAuthenticationSuccess(request, response, authentication);
        
    }
    
}
