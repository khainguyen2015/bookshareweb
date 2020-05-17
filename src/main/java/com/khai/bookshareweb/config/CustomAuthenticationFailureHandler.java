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
import org.springframework.context.MessageSource;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.WebAttributes;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationFailureHandler;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.LocaleResolver;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.authentication.LockedException;


/**
 *
 * @author Acer
 */
//@Component
public class CustomAuthenticationFailureHandler extends SimpleUrlAuthenticationFailureHandler {
    
//    @Autowired
//    private MessageSource messageSource;
    
//    @Autowired
//    private LocaleResolver localeResolver;
    
    @Autowired
    private LoginAttemptService loginAttemptService;
    
    @Override
    public void onAuthenticationFailure(HttpServletRequest request,
            HttpServletResponse response, AuthenticationException exception) throws IOException, ServletException {
        
        this.setDefaultFailureUrl("/login?error=true");
        
        super.onAuthenticationFailure(request, response, exception);
        
        String errorMessage = "";
        
        Exception myCustomException = null;
        
        if(exception.getClass().isAssignableFrom(BadCredentialsException.class)) {
            loginAttemptService.loginFailed(RequestUtils.getIpFromRequest(request));
            errorMessage = "Sai tên tài khoản hoặc mật khẩu";
            myCustomException = new BadCredentialsException(errorMessage);
        }
        
        if(exception.getClass().isAssignableFrom(DisabledException.class)) {
            errorMessage = "Tài khoản chưa xác thực";
            myCustomException = new DisabledException(errorMessage);
        }
        
        if(exception.getClass().isAssignableFrom(LockedException.class)) {
            errorMessage = "Tài khoản đã bị khóa";
            myCustomException = new LockedException(errorMessage);
        }
        
        if(exception.getMessage().equalsIgnoreCase("blocked_ip")) {
            errorMessage = "Đăng nhập thất bại quá 5 lần ! Xin thử lại sau 30 phút nữa";
            myCustomException = new RuntimeException(errorMessage);
        }
        
        request.getSession().setAttribute(WebAttributes.AUTHENTICATION_EXCEPTION, myCustomException);
    }
    
}
