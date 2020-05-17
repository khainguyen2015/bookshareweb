/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.khai.bookshareweb.event.listener;

import com.khai.bookshareweb.entity.UserAccount;
import com.khai.bookshareweb.entity.VerificationToken;
import com.khai.bookshareweb.event.OnRegistrationCompleteEvent;
import com.khai.bookshareweb.service.UserService;
import java.util.UUID;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationListener;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Component;

/**
 *
 * @author Acer
 */

@Component
public class RegistrationListener implements ApplicationListener<OnRegistrationCompleteEvent> {
    
    @Autowired
    private UserService userService;
    
    @Autowired
    private JavaMailSender mailSender;
    

    @Override
    public void onApplicationEvent(OnRegistrationCompleteEvent event) {
        sendRegistrationConfirmEmail(event);
    }
    
    private void sendRegistrationConfirmEmail(OnRegistrationCompleteEvent event) {
        UserAccount userAccount = event.getUserAccount();
        String token = UUID.randomUUID().toString();
        VerificationToken verificationToken = userService.createVerificationToken(userAccount, token);
        
        String recipientAddress = userAccount.getUser().getEmail();
        String subject = "Registration Confirmation";
        String confirmationUrl = event.getAppUrl() + "/registrationConfirm?token=" + token;
        
        String message = "Xác nhận tài khoản Chia Sẻ Sách tại: ";
        SimpleMailMessage email = new SimpleMailMessage();
        email.setSubject(subject);
        email.setText(message + "http://localhost:8080" + confirmationUrl);
        email.setTo(recipientAddress);
        mailSender.send(email);
        
    }

}
