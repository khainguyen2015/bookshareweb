/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.khai.bookshareweb.web;

import java.io.IOException;
import java.util.List;
import javax.servlet.ServletException;
import javax.servlet.http.HttpSession;
import javax.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.khai.bookshareweb.data.TypeRepository;
import com.khai.bookshareweb.data.UserAccountRepository;
import com.khai.bookshareweb.dto.BookDTO;
import com.khai.bookshareweb.dto.UserProfileDto;
import com.khai.bookshareweb.dto.UserUpdatePasswordDto;
import com.khai.bookshareweb.entity.User;
import com.khai.bookshareweb.entity.UserAccount;
import com.khai.bookshareweb.service.BookService;
import com.khai.bookshareweb.service.SecurityService;
import com.khai.bookshareweb.service.UserService;
import java.util.ArrayList;
import java.util.Properties;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.util.HtmlUtils;

/**
 *
 * @author Acer
 */

@Controller
@RequestMapping(value = "/user/*")
public class UserController {
    
    private static final Log LOGGER = LogFactory.getLog(UserController.class);

    private static final int BOOKS_PER_PAGE = 2;

    @Autowired
    private UserAccountRepository userAccountRepository;
    
    @Autowired
    private UserService userService;
    
    @Autowired
    private SecurityService securityService;
    
    @Autowired
    private BookService bookService;
    
    
    @RequestMapping("/")
    public String index() {
        return "redirect:/user/account/profile";
    }
    
    @RequestMapping(value="/account/profile", method=RequestMethod.GET)
    public String profile(Model model, HttpSession session) {
        User user = securityService.getCurrentLoggedUserInSession(session);
        UserProfileDto userProfileDto = new UserProfileDto();
        userProfileDto.setUserName(user.getUserName());
        userProfileDto.setEmail(user.getEmail());
        model.addAttribute("userProfileDto", userProfileDto);
        return "user/account/userProfile";
    }
    
    @RequestMapping(value="/account/change-password", method=RequestMethod.GET)
    public String changePassword(Authentication authentication, Model model) {
        String userAccountName = authentication.getName();
        UserAccount userAccount = userAccountRepository.findByUsername(userAccountName);
        UserUpdatePasswordDto userUpdatePasswordDto = new UserUpdatePasswordDto();
        model.addAttribute("userUpdatePasswordDto", userUpdatePasswordDto);
        return "user/account/userChangePassword";
    }
    
    @RequestMapping(value = {"/book-posted/{page}", "/book-posted"}, method = RequestMethod.GET) 
    public String booksPosted(HttpSession session,
            Model model,
            @PathVariable(name = "page", required = false) Integer page,
            @RequestParam(name = "orderBy", required = false) String orderBy) {
        List<BookDTO> bookList = new ArrayList<>();
        Properties searchProperties = new Properties();
        User user = securityService.getCurrentLoggedUserInSession(session);
        int currentPage = 1;
        
        if(page != null && page > 0) {
            currentPage = page;
        }
        
        if(orderBy != null && !orderBy.isEmpty()) {
            searchProperties.setProperty("orderBy", orderBy);
        } else {
            searchProperties.setProperty("orderBy", "view");
        }
        
        LOGGER.info(currentPage);

        searchProperties.setProperty("page", String.valueOf(currentPage));
        searchProperties.setProperty("maxResults", String.valueOf(BOOKS_PER_PAGE));
        
        bookList = bookService.pagination(searchProperties, user.getUserId());
        int amountOfBooks = bookService.amountOfBooks(user.getUserId());
        
        prepareAttributeForPagination(model, currentPage, amountOfBooks);
        model.addAttribute("books", bookList);
        model.addAttribute("orderBy", orderBy);
        return "user/booksPosted";
    }
    
    @RequestMapping(value= {"/book-posted/results/{page}", "/book-posted/results"}, method = RequestMethod.GET) 
    public String booksPostedSearched(HttpSession session,
            Model model,
            @PathVariable(name = "page", required = false) Integer page,
            @RequestParam(value = "search_key", required = false) String searchKey,
            @RequestParam(value = "orderBy", required = false) String orderBy
            ) {
        
        int currentPage = 1;
        Properties searchProperties = new Properties();
        
        if(page != null && page > 0) {
            currentPage = page;
        }
        
        if(searchKey != null && !searchKey.isEmpty()) {
            searchKey = HtmlUtils.htmlEscape(searchKey);
        } else {
            return "redirect:/user/book-posted";
        }
        
        if(orderBy != null && !orderBy.isEmpty()) {
            searchProperties.setProperty("orderBy", orderBy);
        } else {
            searchProperties.setProperty("orderBy", "view");
        }
        
        searchProperties.setProperty("page", String.valueOf(currentPage));
        searchProperties.setProperty("searchKey", searchKey);
        searchProperties.setProperty("orderBy", orderBy);
        searchProperties.setProperty("maxResults", String.valueOf(BOOKS_PER_PAGE));
        
        User user = securityService.getCurrentLoggedUserInSession(session);
        List<BookDTO> books = bookService.searchBooks(searchProperties, user.getUserId());
        int amountOfBooks = bookService.amountsOfSearchedResults(searchKey, user.getUserId());
        
        prepareAttributeForPagination(model, currentPage, amountOfBooks);
        model.addAttribute("books", books);
        model.addAttribute("searchKey", searchKey);
        model.addAttribute("orderBy", orderBy);
        return "user/bookPostedSearchResults";
    }
    
    private void prepareAttributeForPagination(Model model, int currentPage, int amountOfBooks) {
        int amountOfPages = 0;
        int beginPage, endPage;
        
        if(amountOfBooks % BOOKS_PER_PAGE > 0) {
            amountOfPages = amountOfBooks / BOOKS_PER_PAGE + 1;
        } else {
            amountOfPages = amountOfBooks / BOOKS_PER_PAGE;
        }
        if(currentPage <= 5) {
            beginPage = 1;
        } else {
            beginPage = currentPage - 5;
        }
        
        if(currentPage + 5 >= amountOfPages) {
            endPage = amountOfPages;
        } else {
            endPage = currentPage + 5;
        }
        model.addAttribute("currentPageNum", currentPage);
        model.addAttribute("amountOfPages", amountOfPages);
        model.addAttribute("beginPageNum", beginPage);
        model.addAttribute("endPageNum", endPage);  
    }
    
    
    @RequestMapping(value = "/account/profile", method = RequestMethod.POST)
    public String updateProfile(@ModelAttribute("userProfileDto") UserProfileDto userProfileDto,
            BookDTO bookDto,
            BindingResult bindingResult,
            Authentication authentication,
            Model model,
            @RequestPart("userAvatar") MultipartFile file,
            @RequestParam("userName") String userName,
            HttpSession session,
            RedirectAttributes redirectAttributes) throws IOException, ServletException {
        String userAccountName = authentication.getName();
        
        LOGGER.info("updat profile: " + userProfileDto.getUserName());
        LOGGER.info("updat profile: " + userName);
        LOGGER.info("updat profile: " + bookDto.getBookName());
        
        if(userName != null) {
            return "user/account/userProfile";
        }
        
        UserAccount userAccount = userAccountRepository.findByUsername(userAccountName);
        User user = userAccount.getUser();
        model.addAttribute("user", user);
        
        
        if(bindingResult.hasErrors()) {
            return "user/account/userProfile";
        }
        
        System.out.println(file.getSize());
        if(!file.isEmpty()) {
            String userAvatarUrl = userService.handleUserAvatar(file);   
            System.out.println(userAvatarUrl);
            
            if(!userAvatarUrl.isEmpty()) {
               user.setAvatarUrl(userAvatarUrl);
            } else {
                bindingResult.rejectValue("userAvatar", "", "Ảnh đại diện không hợp lệ");
                return "user/account/userProfile";
            }
        }

        user = userService.updateUser(user, userProfileDto);
        
        if(user == null) {
            redirectAttributes.addFlashAttribute("handleFailed", true);
            return "redirect:/user/account/profile";
        }
        
        session.setAttribute("currentUser", null);
        redirectAttributes.addFlashAttribute("handleSuccess", true);
        return "redirect:/user/account/profile";
    }
    
    
    @RequestMapping(value="/account/change-password", method=RequestMethod.POST)
    public String changePasswordHandle(@Valid @ModelAttribute("userUpdatePasswordDto") UserUpdatePasswordDto userUpdatePasswordDto,
            BindingResult bindingResult, 
            Authentication authentication,
            Model model,
            RedirectAttributes redirectAttributes) {
        String userAccountName = authentication.getName();
        UserAccount userAccount = userAccountRepository.findByUsername(userAccountName);
        User user = userAccount.getUser();
        model.addAttribute("user", user);
        if(bindingResult.hasErrors()) {
            return "user/account/userChangePassword";
        }
        String newPassword = userUpdatePasswordDto.getNewPassword();
        String newPasswordConfirm = userUpdatePasswordDto.getNewPasswordConfirm();
        
        if(!userService.checkValidOldPassword(userAccount, userUpdatePasswordDto.getCurrentPassword())) {
            bindingResult.rejectValue("currentPassword", "", "Mật khẩu hiện tại không khớp");
            return "user/account/userChangePassword";
        }
        
        if(!userService.isConfirmPasswordMatches(newPassword, newPasswordConfirm)) {
            bindingResult.rejectValue("newPasswordConfirm", "", "Mật khẩu xác nhận không khớp");
            return "user/account/userChangePassword";
        }
        userService.changeUserPassword(userAccount, newPassword);
        redirectAttributes.addFlashAttribute("handleSuccess", true);
        return "redirect:/user/account/change-password";
    }
   
}
