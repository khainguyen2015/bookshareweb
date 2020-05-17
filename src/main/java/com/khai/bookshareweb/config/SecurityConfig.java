/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.khai.bookshareweb.config;

import com.khai.bookshareweb.data.UserAccountRepository;
import com.khai.bookshareweb.service.LoginAttemptService;
import java.io.IOException;
import java.util.Arrays;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.sql.DataSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.WebAttributes;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationFailureHandler;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

/**
 *
 * @author Acer
 */

@Configuration
@EnableWebSecurity
@ComponentScan({"com.khai.bookshareweb.config"})
public class SecurityConfig extends WebSecurityConfigurerAdapter {
    
    private DataSource dataSource;
    
    private final UserAccountRepository userAccountRepository;
    
    @Autowired
    private MyUserDetailsService myUserDetailsService;
    
    @Autowired
    public SecurityConfig(DataSource dataSource, UserAccountRepository userAccountRepository) {
        super();
        this.dataSource = dataSource;
        this.userAccountRepository = userAccountRepository;
    }
    
    @Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
        auth.userDetailsService(myUserDetailsService).passwordEncoder(new BCryptPasswordEncoder());
    }
    
    
    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http
            .authorizeRequests()
                .antMatchers("/user/**")
                .hasAnyAuthority("ROLE_USER", "ROLE_ADMIN")
//                .antMatchers("/admin/**")
//                .hasAnyAuthority("ROLE_ADMIN")
//                .antMatchers("/book/create-new-book")
//                .hasAnyAuthority("ROLE_USER", "ROLE_ADMIN")
//                .antMatchers("/book/update-book")
//                .hasAnyAuthority("ROLE_USER", "ROLE_ADMIN")
            .and()
            .formLogin()
                .failureHandler(customAuthenticationFailureHandler())
                .successHandler(customAuthenticationSucessHandler())
                .loginPage("/login")
            .and()
            .logout()
                .deleteCookies("remember-me")
            .and()
            .rememberMe()
                .tokenValiditySeconds(345600)
                .key("bookShareWebKey");
    }
    
   
    @Bean
    public AuthenticationFailureHandler customAuthenticationFailureHandler() {
        System.out.println("Create customAuthenticationFailureHandler");
        return new CustomAuthenticationFailureHandler();
    }
    
    @Bean
    public AuthenticationSuccessHandler customAuthenticationSucessHandler() {
        System.out.println("Create customAuthenticationSucessHandler");
        return new CustomAuthenticationSucessHandler();
    }
    
    @Bean
    @Override
    public AuthenticationManager authenticationManagerBean() throws Exception {
       return super.authenticationManagerBean();
    }
    
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
    
    @Bean
    public LoginAttemptService loginAttemptService() {
        return new LoginAttemptService();
    }
    
    @Bean
    CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        configuration.setAllowedOrigins(Arrays.asList("http://localhost"));
        configuration.setAllowedMethods(Arrays.asList("GET","POST","OPTIONS","DELETE"));
        configuration.setAllowCredentials(Boolean.TRUE);
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }
    
}
