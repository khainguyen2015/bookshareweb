/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.khai.bookshareweb.config;

import com.khai.bookshareweb.interceptor.LoggedUserInterceptor;
import com.khai.bookshareweb.service.SecurityService;
import java.util.Locale;
import java.util.Properties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.ViewResolver;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.DefaultServletHandlerConfigurer;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import org.thymeleaf.spring5.SpringTemplateEngine;
import org.thymeleaf.spring5.templateresolver.SpringResourceTemplateResolver;
import org.thymeleaf.spring5.view.ThymeleafViewResolver;
import org.thymeleaf.templatemode.TemplateMode;
import nz.net.ultraq.thymeleaf.LayoutDialect;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.context.annotation.PropertySource;
import org.springframework.context.support.ReloadableResourceBundleMessageSource;
import org.springframework.context.support.ResourceBundleMessageSource;
import org.springframework.core.env.Environment;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.springframework.security.web.session.HttpSessionEventPublisher;
import org.springframework.validation.beanvalidation.LocalValidatorFactoryBean;
import org.springframework.web.multipart.MultipartResolver;
import org.springframework.web.multipart.commons.CommonsMultipartResolver;
import org.springframework.web.multipart.support.StandardServletMultipartResolver;
import org.springframework.web.servlet.HandlerExceptionResolver;
import org.springframework.web.servlet.LocaleResolver;
import org.springframework.web.servlet.handler.SimpleMappingExceptionResolver;
import org.springframework.web.servlet.i18n.CookieLocaleResolver;
import org.springframework.web.servlet.i18n.LocaleChangeInterceptor;
import org.thymeleaf.extras.springsecurity5.dialect.SpringSecurityDialect;

/**
 *
 * @author Acer
 */

@Configuration
@EnableWebMvc
@PropertySource("classpath:application.properties")
@ComponentScan({
    "com.khai.bookshareweb.web", 
    "com.khai.bookshareweb.service", 
    
    "com.khai.bookshareweb.common",
    "com.khai.bookshareweb.interceptor",
//    "com.khai.bookshareweb.web.rest",
    })
public class WebConfig implements WebMvcConfigurer {
    
    @Autowired
    private Environment env;
    
    @Autowired
    private LoggedUserInterceptor loggedUserInterceptor;
    
    @Autowired
    private SecurityService securityService;
    
//    @Bean
//    public ViewResolver viewResolver() {
//        InternalResourceViewResolver viewResolver = new InternalResourceViewResolver();
//        viewResolver.setPrefix("WEB-INF/views/");
//        viewResolver.setSuffix(".jsp");
//        viewResolver.setExposeContextBeansAsAttributes(true);
//        return viewResolver;
//    }
    
    @Override
    public void addCorsMappings(CorsRegistry registry) {
            registry.addMapping("/**").allowedOrigins("http://localhost").allowCredentials(true);
    }
    
    @Override
    public void configureDefaultServletHandling(DefaultServletHandlerConfigurer configurer ) {
        configurer.enable();
    }
    
    @Bean
    public HandlerExceptionResolver  simpleMappingExceptionResolver() {
        MyExceptionResolver MyExceptionResolver = new MyExceptionResolver(securityService);
        
        Properties exceptionMappings = new Properties();
        exceptionMappings.put("java.lang.Exception", "errors/genericError");
        exceptionMappings.put(RuntimeException.class.getName(), "errors/genericError");
        
        MyExceptionResolver.setExceptionMappings(exceptionMappings);
        
        Properties statusCodes = new Properties();
        statusCodes.put("errors/404", "404");
        statusCodes.put("errors/genericError", "500");
        
        MyExceptionResolver.setStatusCodes(statusCodes);
        
        return MyExceptionResolver;
    }
    
    @Bean
    public ViewResolver viewResolver(SpringTemplateEngine templateEngine) {
        ThymeleafViewResolver viewResolver = new ThymeleafViewResolver();
        viewResolver.setTemplateEngine(templateEngine);
        viewResolver.setCharacterEncoding("UTF-8");
        return viewResolver;
    }
    
    @Bean
    public SpringTemplateEngine templateEngine(SpringResourceTemplateResolver resolver) {
        SpringTemplateEngine templateEngine = new SpringTemplateEngine();
        templateEngine.addTemplateResolver(resolver);
        templateEngine.addDialect(new LayoutDialect());
        templateEngine.addDialect(new SpringSecurityDialect());
        return templateEngine;  
    }
    
    @Bean
    public SpringResourceTemplateResolver templateResolver() {
        SpringResourceTemplateResolver templateResolver = new SpringResourceTemplateResolver();
        templateResolver.setCacheable(false);
        templateResolver.setPrefix("/WEB-INF/templates/");
        templateResolver.setSuffix(".html");
        templateResolver.setTemplateMode(TemplateMode.HTML);
        templateResolver.setCharacterEncoding("UTF-8");
        return templateResolver;
    }
    
    
    @Bean
    public MessageSource messageSource() {
        ReloadableResourceBundleMessageSource  messageSource = new ReloadableResourceBundleMessageSource();
        messageSource.setBasename("classpath:/messages");
        messageSource.setDefaultEncoding("UTF-8");
        return messageSource;
    }
    
    
    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(new LoggerInterceptor());
        
        LocaleChangeInterceptor localeChangeInceptor = new LocaleChangeInterceptor();
        localeChangeInceptor.setParamName("language");
        registry.addInterceptor(localeChangeInceptor).addPathPatterns("/*");
        registry.addInterceptor(loggedUserInterceptor).excludePathPatterns("/api/**");
    }
    
    
    @Bean
    public JavaMailSender getJavaMailSender() {
        JavaMailSenderImpl mailSender = new JavaMailSenderImpl();
        mailSender.setHost(env.getProperty("spring.mail.host"));
        mailSender.setPort(Integer.parseInt(env.getProperty("spring.mail.port")));

        mailSender.setUsername(env.getProperty("spring.mail.username"));
        mailSender.setPassword(env.getProperty("spring.mail.password"));

        Properties props = mailSender.getJavaMailProperties();
        props.put("mail.transport.protocol", env.getProperty("spring.mail.properties.mail.transport.protocol"));
        props.put("mail.smtp.auth", env.getProperty("spring.mail.properties.mail.smtp.auth"));
        props.put("mail.smtp.starttls.enable", env.getProperty("spring.mail.properties.mail.smtp.starttls.enable"));
        props.put("mail.debug", env.getProperty("spring.mail.properties.mail.debug"));
        props.put("mail.smtp.ssl.trust", env.getProperty("spring.mail.properties.mail.smtp.ssl.trust"));

        return mailSender;
    }
    
    @Bean
    public LocaleResolver localeResolver() {
        CookieLocaleResolver cookieLocaleResolver = new CookieLocaleResolver();
        cookieLocaleResolver.setDefaultLocale(Locale.US);
        cookieLocaleResolver.setCookieMaxAge(5*60);
        return cookieLocaleResolver;
    }
    
    @Bean
    public MultipartResolver multipartResolver() {
        return new StandardServletMultipartResolver();
    }
    
    
}
