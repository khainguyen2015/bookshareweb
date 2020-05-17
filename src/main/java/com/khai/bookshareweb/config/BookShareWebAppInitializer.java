/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.khai.bookshareweb.config;

import javax.servlet.MultipartConfigElement;
import javax.servlet.ServletContext;
import javax.servlet.ServletRegistration;
import javax.servlet.ServletRegistration.Dynamic;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.servlet.support.AbstractAnnotationConfigDispatcherServletInitializer;

/**
 *
 * @author Acer
 */
public class BookShareWebAppInitializer extends AbstractAnnotationConfigDispatcherServletInitializer {
    
    private static final String TMP_FOLDER = "/tmp";
    private static final int MAX_UPLOAD_SIZE = 1000000;
    
    @Override
    protected String[] getServletMappings() {
        return new String[] {"/"};
    }
    
    @Override
    protected Class[] getRootConfigClasses() {
        return new Class[] {RootConfig.class, SecurityConfig.class};
    }
    
    @Override
    protected Class[] getServletConfigClasses() {
        return new Class[] {WebConfig.class};
    }
    
    @Override
    protected void customizeRegistration(Dynamic registration) {
        
        MultipartConfigElement multipartConfigElement = new MultipartConfigElement(
            TMP_FOLDER,
            MAX_UPLOAD_SIZE,
            MAX_UPLOAD_SIZE * 2,
            MAX_UPLOAD_SIZE / 2
        );
        registration.setMultipartConfig(multipartConfigElement);
    }
    
   
}
