/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.khai.bookshareweb.config;

import java.io.File;
import javax.servlet.FilterRegistration.Dynamic;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import org.springframework.security.web.session.HttpSessionEventPublisher;
import org.springframework.web.WebApplicationInitializer;
import org.springframework.web.context.request.RequestContextListener;
import org.springframework.web.filter.CharacterEncodingFilter;

/**
 *
 * @author Acer
 */
public class BookShareWebInitializer implements WebApplicationInitializer {
    
    private static final String TMP_FOLDER = "/tmp";
    
    @Override
    public void onStartup(ServletContext servletContext) throws ServletException {
//        CharacterEncodingFilter encodingFilter = new CharacterEncodingFilter();
//        encodingFilter.setEncoding("UTF-8");
//        encodingFilter.setForceEncoding(true);
//        Dynamic filter = servletContext.addFilter("encodingFilter", encodingFilter);
//        filter.addMappingForUrlPatterns(null, false, "/*");
        
        servletContext.addListener(new RequestContextListener());
        servletContext.addListener(new HttpSessionEventPublisher());
        
        File f = (File)servletContext.getAttribute("javax.servlet.context.tempdir");
        String tmpFolderPath = f.getAbsolutePath() + TMP_FOLDER;
        System.out.println(tmpFolderPath);
        File file = new File(tmpFolderPath);
        if(!file.exists()) {
            file.mkdir();
        }
    }
    
}
