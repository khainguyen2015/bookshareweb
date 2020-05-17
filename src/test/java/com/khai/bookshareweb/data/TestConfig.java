/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.khai.bookshareweb.data;


import com.khai.bookshareweb.common.GoogleUtils;
import com.khai.bookshareweb.service.BookService;
import com.khai.bookshareweb.service.BookServiceImpl;
import com.khai.bookshareweb.service.MultipartFileService;
import com.khai.bookshareweb.service.MultipartFileServiceImpl;
import com.khai.bookshareweb.service.SecurityService;
import com.khai.bookshareweb.service.SecurityServiceImpl;
import com.khai.bookshareweb.service.UserService;
import com.khai.bookshareweb.web.AdminController;
import com.khai.bookshareweb.web.HomeController;
import com.khai.bookshareweb.web.UserController;
import com.khai.bookshareweb.web.rest.BookRestController;
import org.mockito.Mockito;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 *
 * @author Acer
 */

//@Configuration
@EnableWebMvc
@Import({AdminController.class})
public class TestConfig implements WebMvcConfigurer {
    
//    @Bean
//    public BookRepository bookRepository() {
//        return Mockito.mock(HibernateBookRepository.class);
//    }
    
    @Bean
    public BookService bookService() {
        return Mockito.mock(BookServiceImpl.class);
    }

    
    @Bean
    public SecurityService securityService() {
        return Mockito.mock(SecurityServiceImpl.class);
    }
    
    @Bean
    public MultipartFileService multipartFileService() {
        return Mockito.mock(MultipartFileServiceImpl.class);
    }
    
    @Bean
    public UserService userService() {
        return Mockito.mock(UserService.class);
    }
    
    @Bean
    public GoogleUtils googleUtils() {
        return Mockito.mock(GoogleUtils.class);
    }
    
}
