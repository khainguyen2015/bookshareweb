/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.khai.bookshareweb.service;

import com.khai.bookshareweb.data.PasswordResetTokenRepository;
import com.khai.bookshareweb.data.UserAccountRepository;
import com.khai.bookshareweb.data.UserRepository;
import com.khai.bookshareweb.data.VerificationTokenRepository;
import com.khai.bookshareweb.dto.UserDto;
import com.khai.bookshareweb.dto.UserProfileDto;
import com.khai.bookshareweb.entity.PasswordResetToken;
import com.khai.bookshareweb.entity.User;
import com.khai.bookshareweb.entity.UserAccount;
import com.khai.bookshareweb.entity.VerificationToken;
import com.khai.bookshareweb.service.exception.AccountExistsException;
import com.khai.bookshareweb.service.exception.EmailExistsException;
import java.util.Locale;
import java.util.UUID;
import javax.servlet.ServletContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

/**
 *
 * @author Acer
 */
@Service
public class UserServiceImpl implements UserService {
    
    private static final String FILE_SEPARATOR = System.getProperty("file.separator");
    
    private static final String DEFAULT_USER_AVATAR_URL = "images/user_avatars/avatar-blank.jpg";
    
    private static final int DEFAULT_USER_ROLE = 2;
    
    private static final String USER_AVATAR_FILE_PATH = "public" + FILE_SEPARATOR + "images" + FILE_SEPARATOR + "user_avatars";
    
    private static final String USER_AVATAR_URL_PREFIX_PATH = "images/user_avatars/";
    
    @Autowired
    private ServletContext servletContext;
    
    @Autowired
    private UserAccountRepository userAccountRepository;
    
    @Autowired
    private UserRepository userRepository;
    
    @Autowired
    private VerificationTokenRepository verificationTokenRepository;
    
    @Autowired
    private PasswordResetTokenRepository hibernatePasswordResetTokenRepository;
    
    @Autowired
    private PasswordEncoder passwordEncoder;
    
    @Autowired
    private MultipartFileService multipartFileService;

    
    public UserAccount registerNewUserAccount(UserDto userDto) throws EmailExistsException, AccountExistsException {
        if(emailExists(userDto.getEmail())) {
            throw new EmailExistsException("form.register.error.email.emailExist");
        }
        
        if(accountNameExists(userDto.getAccountName())) {
            throw new AccountExistsException("Tên tài khoản đã tồn tại");
        }
        
        User user = new User();
        user.setUserName(userDto.getUserName());
        user.setEmail(userDto.getEmail());
        if(userDto.getPicture() == null) {
            user.setAvatarUrl(DEFAULT_USER_AVATAR_URL);
        } else {
            user.setAvatarUrl(userDto.getPicture());
        }
        String password = new BCryptPasswordEncoder().encode(userDto.getPassword());
        String accountName = userDto.getAccountName();
        
        UserAccount userAccount = new UserAccount(accountName, password, user, DEFAULT_USER_ROLE);
        user.setUserAccount(userAccount);
        
        user = userRepository.save(user);
        
        userAccount = userAccountRepository.findByUsername(accountName);
        
        return userAccount;
    }
    
    @Override
    public boolean emailExists(String email) {
        return getUserByEmail(email) != null;
    }
    
    @Override
    public User getUserByEmail(String email) {
        User user = userRepository.findByEmail(email);
        return user;
    }
    
    private boolean accountNameExists(String accountName) {
        UserAccount userAccount = userAccountRepository.findByUsername(accountName);
        return userAccount != null;
    }
    
    @Override
    public VerificationToken createVerificationToken(UserAccount userAccount, String token) {
        VerificationToken verificationToken = new VerificationToken();
        verificationToken.setToken(token);
        verificationToken.setUserAccount(userAccount);
        verificationToken.setExpiryDate(VerificationToken.calculateExpiryDate(VerificationToken.IN_MINUTES_EXPIRATION));
        verificationToken = verificationTokenRepository.save(verificationToken);
        return verificationToken;
    }
    
    @Override
    public VerificationToken generateNewVerificationToken(String existingToken) {
        VerificationToken verificationToken = verificationTokenRepository.findByToken(existingToken);
        if(verificationToken == null) {
            return null;
        }
        verificationToken.setToken(generateToken());
        verificationToken = verificationTokenRepository.save(verificationToken);
        return verificationToken;
    }
    
    @Override
    public PasswordResetToken createPasswordResetToken(UserAccount userAccount, String token) {
        PasswordResetToken passwordResetToken = new PasswordResetToken();
        passwordResetToken.setToken(token);
        passwordResetToken.setUserAccount(userAccount);
        passwordResetToken.setExpiryDate(PasswordResetToken.calculateExpiryDate(PasswordResetToken.IN_MINUTES_EXPIRATION));
        passwordResetToken = hibernatePasswordResetTokenRepository.save(passwordResetToken);
        return passwordResetToken;
    }
    
    @Override
    public UserAccount changeUserPassword(UserAccount userAccount, String newPassword) {
        newPassword = passwordEncoder.encode(newPassword);
        userAccount.setPassword(newPassword);
        return userAccountRepository.update(userAccount);
    }
    
    @Override
    public boolean checkValidOldPassword(UserAccount userAccount, String oldPassword) {
        System.out.println(userAccount.getPassword());
        return passwordEncoder.matches(oldPassword, userAccount.getPassword());
    }
    
    public User updateUser(User user, UserProfileDto userProfileDto) {
        user.setUserName(userProfileDto.getUserName());
        user.setEmail(userProfileDto.getEmail());
        user = userRepository.update(user);
        return user;
    }
    
    @Override
    public boolean isConfirmPasswordMatches(String password, String confirmPassword) {
        return confirmPassword.equalsIgnoreCase(password);
    }
    
    private String generateToken() {
        return UUID.randomUUID().toString();
    }
    
    public String handleUserAvatar(MultipartFile file) {
        if(file.isEmpty()) return "";
        String path = servletContext.getRealPath(USER_AVATAR_FILE_PATH);
        String fileName = multipartFileService.storeImageToSpecificPath(file, path);
        return USER_AVATAR_URL_PREFIX_PATH + fileName;
    }
    
}
