/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.khai.bookshareweb.interceptor;

import com.khai.bookshareweb.data.UserAccountRepository;
import com.khai.bookshareweb.entity.User;
import com.khai.bookshareweb.service.SecurityService;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

/**
 *
 * @author Acer
 */
@Component
public class LoggedUserInterceptor extends HandlerInterceptorAdapter {
    
    @Autowired
    private SecurityService securityService;

    @Override
    public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler, ModelAndView modelAndView) {
        System.out.println("Logged user interceptor");
        User user = securityService.getCurrentLoggedUserInSession(request.getSession());
        if(user == null) {
            return;
        }
        modelAndView.addObject("user", user);
    }
    


}
