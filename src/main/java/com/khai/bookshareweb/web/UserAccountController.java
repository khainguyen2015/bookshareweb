/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.khai.bookshareweb.web;

import com.khai.bookshareweb.data.HibernateUserRepository;
import com.khai.bookshareweb.data.PasswordResetTokenRepository;
import com.khai.bookshareweb.data.UserRepository;
import com.khai.bookshareweb.dto.PasswordDTO;
import com.khai.bookshareweb.entity.PasswordResetToken;
import com.khai.bookshareweb.entity.User;
import com.khai.bookshareweb.entity.UserAccount;
import com.khai.bookshareweb.entity.VerificationToken;
import com.khai.bookshareweb.service.UserService;
import java.util.Calendar;
import java.util.Locale;
import java.util.UUID;
import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.LocaleResolver;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.springframework.web.util.HtmlUtils;

/**
 *
 * @author Acer
 */

//@Controller
public class UserAccountController {
    
    @Autowired
    private PasswordResetTokenRepository hibernatePasswordResetTokenRepository;
    
    @Autowired
    private UserRepository hibernateUserRepository;
    
    @Autowired
    private JavaMailSender mailSender;
    
    @Autowired
    private UserService userService;
    
    @Autowired
    private LocaleResolver localeResolver;
    
    @Autowired
    private MessageSource messageSource;
    
    private static final Logger LOGGER = LoggerFactory.getLogger(UserAccountController.class);
        
    @RequestMapping(value="/password_reset", method = RequestMethod.GET)
    public String resetPassword(@RequestParam(name = "token", required = false) String token, Model model, HttpServletRequest request) {
        
        String errorMessages = validatePasswordResetToken(token, request.getLocale());
        if(!errorMessages.isEmpty()) {
            model.addAttribute("errorMessages", errorMessages);
            return "passwordReset";
        }else {
            model.addAttribute("passwordResetToken", token);
            model.addAttribute("password", new PasswordDTO());
            return "changePassword";
        }
    }
    
    @RequestMapping(value="/password_reset", method = RequestMethod.POST)
    public String resetPasswordProcess(@RequestParam(name = "email") String userEmail, Model model, HttpServletRequest request) {
        
        if(!(userEmail != null && userEmail.length() > 0)) {
            model.addAttribute("errorMessages", messageSource.getMessage("resetPassword.error.email.format", null, request.getLocale()));
            return "passwordReset";
        }
        User user = hibernateUserRepository.findByEmail(userEmail);
        if(user == null) {
            model.addAttribute("errorMessages", messageSource.getMessage("resetPassword.error.email.emailNotExist", null, request.getLocale()));
            return "passwordReset";
        }
        if(!user.getUserAccount().isEnable()) {
            model.addAttribute("errorMessages", "Tài khoản chưa được xác thực");
            return "passwordReset";
        }
        String token = UUID.randomUUID().toString();
        userService.createPasswordResetToken(user.getUserAccount(), token);
        String appUrl = "http://" + request.getServerName() + ":" + request.getServerPort() + request.getContextPath();
        SimpleMailMessage email = constructPasswordResetEmail(appUrl, token, user);
        mailSender.send(email);
        model.addAttribute("messages", "Yêu cầu đổi mật khẩu thành công! Vui lòng kiểm tra email của bạn và làm theo hướng dẫn");
        return "sendPasswordResetEmailSuccess";
    }
    
    
    @RequestMapping(value="/change_password", method = RequestMethod.POST)
    public String changePasswordProcess(@RequestParam(name = "token") String token,
             @Valid @ModelAttribute("password") PasswordDTO passwordDTO, 
             BindingResult bindingResult, Model model, HttpServletRequest request) {
        Locale locale = localeResolver.resolveLocale(request);
        String errorMessages = validatePasswordResetToken(token, locale);
        if(!errorMessages.isEmpty()) {
            LOGGER.info("Password reset failed");
            model.addAttribute("errorMessages", errorMessages);
            return "passwordReset";
        }
        
        if(bindingResult.hasErrors()) {
            LOGGER.info("Password reset failed");
            model.addAttribute("passwordResetToken", token);
            return "changePassword";
        }

        if(!validateNewPassword(passwordDTO, bindingResult)) {
            LOGGER.info("Password reset failed");
            model.addAttribute("passwordResetToken", token);
            return "changePassword";
        }
        PasswordResetToken passwordResetToken = hibernatePasswordResetTokenRepository.findByToken(token);
        UserAccount userAccount = passwordResetToken.getUserAccount();
        userService.changeUserPassword(userAccount, passwordDTO.getPassword());
        model.addAttribute("messages", "Đổi mật khẩu thành công");
        return "changePasswordSuccess";

    }
    
    @RequestMapping(value = "/verification_new_account", method = RequestMethod.GET)
    public String verificationNewAccount(Model model) {
        model.addAttribute("reVerification", true);
        return "newAccountVerification";
    }
    
    @RequestMapping(value = "/verification_new_account", method = RequestMethod.POST)
    public String reVerificationNewAccountHandle(Model model,
            @RequestParam(name = "email") String userEmail,
            HttpServletRequest request) {
        if(!(userEmail != null && userEmail.length() > 0)) {
            model.addAttribute("errorMessages","Email không hợp lệ");
            model.addAttribute("reVerification", true);
            return "newAccountVerification";
        }
        
        userEmail = HtmlUtils.htmlEscape(userEmail);
        User user = userService.getUserByEmail(userEmail);
        if(user == null) {
            model.addAttribute("errorMessages","Email không tồn tại");
            model.addAttribute("reVerification", true);
            return "newAccountVerification";
        }
        
        UserAccount userAccount = user.getUserAccount();
        if(userAccount.isEnable()) {
            model.addAttribute("errorMessages","Tài khoản đã được xác thực");
            model.addAttribute("reVerification", true);
            return "newAccountVerification";
        }

        String token = UUID.randomUUID().toString();
        VerificationToken verificationToken = userService.createVerificationToken(userAccount, token);
        String appUrl = "http://" + request.getServerName() + ":" + request.getServerPort() + request.getContextPath();
        SimpleMailMessage email = constructVerificationEmail(appUrl, verificationToken.getToken(), user);
        mailSender.send(email);
        model.addAttribute("messages", "Yêu cầu xác thực tài khoản thành công! Vui lòng kiểm tra email của bạn và làm theo hướng dẫn");
        return "newAccountVerification";
    }
    
    
    private String validatePasswordResetToken(String token, Locale locale) {
        if(token == null) {
            return messageSource.getMessage("resetPassword.error.invalidToken", null, locale);
        }
        if(token.isEmpty()) {
            return messageSource.getMessage("resetPassword.error.invalidToken", null, locale);
        }
        
        PasswordResetToken passwordResetToken = null;
        passwordResetToken = hibernatePasswordResetTokenRepository.findByToken(token);
        
        if(passwordResetToken == null) {
            return messageSource.getMessage("resetPassword.error.invalidToken", null, locale);
        }
        Calendar cal = Calendar.getInstance();
        if((passwordResetToken.getExpiryDate().getTime() - cal.getTime().getTime()) <= 0) {
            return messageSource.getMessage("resetPassword.error.expiredToken", null, locale);
        }
        return "";
    }
    
    private SimpleMailMessage constructVerificationEmail(String appUrl, String token, User user) {
        String confirmationUrl = appUrl + "/registrationConfirm?token=" + token;
        String recipientAddress = user.getEmail();
        String subject = "Verification Account";
        String message = "Vào link sau để xác minh tài khoản Chia Sẻ Sách của bạn: ";
        SimpleMailMessage email = new SimpleMailMessage();
        email.setSubject(subject);
        email.setText(message + confirmationUrl);
        email.setTo(recipientAddress);
        return email;
    }
    
    
    private SimpleMailMessage constructPasswordResetEmail(String appUrl, String token, User user) {
        String confirmationUrl = appUrl + "/password_reset?token=" + token;
        String recipientAddress = user.getEmail();
        String subject = "Password Reset";
        String message = "Vào link sau để đổi mật khẩu tài khoản Chia Sẻ Sách của bạn: ";
        SimpleMailMessage email = new SimpleMailMessage();
        email.setSubject(subject);
        email.setText(message + confirmationUrl);
        email.setTo(recipientAddress);
        return email;
    }
    
    private boolean validateNewPassword(PasswordDTO passwordDto, BindingResult bindingResult) {
        String passwordRegex = "^[A-Za-z0-9]{5,50}$";
        String newPassword = passwordDto.getPassword();
        String newPasswordConfirm = passwordDto.getPasswordConfirm();
        if(!newPassword.matches(passwordRegex)) {
            bindingResult.rejectValue("password", "", "Mật khẩu không đúng định dạng");
            return false;
        }
        
        if(!newPasswordConfirm.matches(passwordRegex)) {
            bindingResult.rejectValue("passwordConfirm", "", "Mật khẩu nhập lại không đúng định dạng");
            return false;
        }
        
        if(!newPassword.equals(newPasswordConfirm)) {
            bindingResult.rejectValue("passwordConfirm", "", "Mật nhập lại không khớp");
            return false;
        }
        
        return true;
    }
    
}
