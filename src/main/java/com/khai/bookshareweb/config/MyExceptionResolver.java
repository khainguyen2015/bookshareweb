/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.khai.bookshareweb.config;

import com.khai.bookshareweb.entity.User;
import com.khai.bookshareweb.service.SecurityService;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.handler.SimpleMappingExceptionResolver;

/**
 *
 * @author Acer
 */
public class MyExceptionResolver extends SimpleMappingExceptionResolver {
    
    private SecurityService securityService;
    
    public MyExceptionResolver() {
        
    }
    
    public MyExceptionResolver(SecurityService securityService) {
        this.securityService = securityService;
    }
    
    protected ModelAndView doResolveException(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) {
        ModelAndView modelAndView = super.doResolveException(request, response, handler, ex);
        if(modelAndView != null) {
            User user = securityService.getCurrentLoggedUserInSession(request.getSession());
            if(user != null) {
                modelAndView.addObject("user", user);
            }
        }
        return modelAndView;
    }
    
}
