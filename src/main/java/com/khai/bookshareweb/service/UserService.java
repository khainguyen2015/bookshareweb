/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.khai.bookshareweb.service;

import com.khai.bookshareweb.dto.UserDto;
import com.khai.bookshareweb.dto.UserProfileDto;
import com.khai.bookshareweb.entity.PasswordResetToken;
import com.khai.bookshareweb.entity.User;
import com.khai.bookshareweb.entity.UserAccount;
import com.khai.bookshareweb.entity.VerificationToken;
import com.khai.bookshareweb.service.exception.AccountExistsException;
import com.khai.bookshareweb.service.exception.EmailExistsException;
import org.springframework.web.multipart.MultipartFile;

/**
 *
 * @author Acer
 */
public interface UserService {
    
    public UserAccount registerNewUserAccount(UserDto userDto) throws EmailExistsException, AccountExistsException; 
    
    public VerificationToken createVerificationToken(UserAccount userAccount, String token);
    
    public VerificationToken generateNewVerificationToken(String existingToken);
    
    public PasswordResetToken createPasswordResetToken(UserAccount userAccount, String token);
    
    public UserAccount changeUserPassword(UserAccount userAccount, String newPassword);
    
    public boolean checkValidOldPassword(UserAccount userAccount, String oldPassword);
    
    public boolean isConfirmPasswordMatches(String password, String confirmPassword);
    
    public String handleUserAvatar(MultipartFile multipartFile);
    
    public User updateUser(User user, UserProfileDto userProfileDto);
    
    public boolean emailExists(String email);
    
    public User getUserByEmail(String email);
    
}
