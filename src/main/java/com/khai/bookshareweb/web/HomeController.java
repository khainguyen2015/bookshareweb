/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.khai.bookshareweb.web;


import com.khai.bookshareweb.common.GooglePojo;
import com.khai.bookshareweb.common.GoogleUtils;
import com.khai.bookshareweb.data.UserAccountRepository;
import com.khai.bookshareweb.data.UserRepository;
import com.khai.bookshareweb.data.VerificationTokenRepository;
import com.khai.bookshareweb.dto.BookDTO;
import com.khai.bookshareweb.dto.UserDto;
import com.khai.bookshareweb.entity.User;
import com.khai.bookshareweb.entity.UserAccount;
import com.khai.bookshareweb.entity.VerificationToken;
import com.khai.bookshareweb.event.OnRegistrationCompleteEvent;
import com.khai.bookshareweb.service.BookService;
import com.khai.bookshareweb.service.SecurityService;
import com.khai.bookshareweb.service.UserService;
import com.khai.bookshareweb.service.exception.AccountExistsException;
import com.khai.bookshareweb.service.exception.EmailExistsException;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.List;
import java.util.Properties;
import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import org.apache.commons.logging.LogFactory;
import org.apache.commons.logging.Log;
import org.apache.http.client.ClientProtocolException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEvent;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.springframework.web.util.HtmlUtils;

/**
 *
 * @author Acer
 */

@Controller
@RequestMapping(value = "/")
public class HomeController {
    
    private static final int BOOKS_PER_PAGE = 10;
    
    
    @Autowired
    private BookService bookService;
    
    @Autowired
    private UserRepository userRepository;
    
    @Autowired
    private UserAccountRepository userAccountRepository;
    
    @Autowired
    private UserService userService;
    
    @Autowired
    private ApplicationEventPublisher applicationEventPublisher;
    
    @Autowired
    private VerificationTokenRepository verificationTokenRepository;
    
    @Autowired
    private GoogleUtils googleUtils;
    
    @Autowired
    private JavaMailSender mailSender;
    
    @Autowired
    private SecurityService securityService;
    
    
    private RequestMappingHandlerMapping requestMappingHandlerMapping;
    
    private static final Log LOGGER = LogFactory.getLog(HomeController.class);
    
   
    public HomeController() {
        
    }
    
//    @Autowired
//    public HomeController(RequestMappingHandlerMapping requestMappingHandlerMapping) {
//        this.requestMappingHandlerMapping = requestMappingHandlerMapping;
//    }
    
    @RequestMapping(value = {"/{page}", "/"}, method = RequestMethod.GET)
    public String home(Model model,
            @PathVariable(name = "page", required = false) Integer page,
            HttpServletRequest request) {
        
        Enumeration enums = request.getSession().getAttributeNames();
        while(enums.hasMoreElements()) {
            System.out.println("Session attribute name: " + enums.nextElement());
            System.out.println("Session attribute value: " + request.getSession().getAttribute("SPRING_SECURITY_SAVED_REQUEST"));
        }
        List<BookDTO> bookList = new ArrayList<>();
        
        int currentPage = 1;
        if(page != null) {
            currentPage = page;
        }
        
        bookList = bookService.pagination(BOOKS_PER_PAGE, currentPage);
        int amountOfBooks = bookService.amountOfBooks();
        
        prepareAttributeForPagination(model, currentPage, amountOfBooks);
        
        model.addAttribute("bookList", bookList);
        LOGGER.info("home: " + Thread.currentThread().getName());
        return "index";
    }
    
    @RequestMapping(value="/results", method = RequestMethod.GET)
    public String searchBook(Model model,
            @RequestParam(value = "search_key", required = false) String searchKey,
            @RequestParam(value = "page", required = false) String page,
            @RequestParam(value = "orderBy", required = false) String orderBy) {
        if(searchKey == null) {
            return "redirect:/";
        }
        
        if(searchKey.isEmpty()) {
            return "redirect:/";
        }
        
        int currentPage = 1;
        
        if(page != null) {
            try {
                currentPage = Integer.parseInt(page);
            } catch(NumberFormatException ex) {
                LOGGER.error(ex.getMessage());
                return "redirect:/";
            }
        }
        
        if(currentPage <= 0) {
            currentPage = 1;
        }
        
        if(!(orderBy != null && !orderBy.isEmpty())) {
            orderBy = "view";
        }
        
        searchKey = HtmlUtils.htmlEscape(searchKey);
        
        Properties searchProperties = new Properties();
        searchProperties.setProperty("searchKey", searchKey);
        searchProperties.setProperty("orderBy", orderBy);
        searchProperties.setProperty("page", String.valueOf(currentPage));
        searchProperties.setProperty("maxResult", String.valueOf(BOOKS_PER_PAGE));
          
        List<BookDTO> bookDTOs = bookService.searchBooks(searchProperties);
        int amountOfBooks = bookService.amountsOfSearchedResults(searchKey);
        
        model.addAttribute("bookList", bookDTOs);
        model.addAttribute("searchKey", searchKey);
        model.addAttribute("orderBy", orderBy);
        
        prepareAttributeForPagination(model, currentPage, amountOfBooks);
        
        return "searchResult";
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
    
    @RequestMapping(value="/login", method=RequestMethod.GET)
    public String login(@RequestParam(value = "error", required = false) boolean loginError,
            HttpServletRequest request,
            Model model,
            RedirectAttributes redirectAttributes) {
        System.out.println("login");
        Enumeration enums = request.getSession().getAttributeNames();
        while(enums.hasMoreElements()) {
            Object o = enums.nextElement();
            System.out.println("Session attribute name: " + o);
            System.out.println("Session attribute class: " + o.getClass());
        }
        if(loginError) {
            Exception loginException = (Exception)request.getSession().getAttribute("SPRING_SECURITY_LAST_EXCEPTION");
            if(loginException == null) {
                return "login";
            }
            if(loginException.getClass().isAssignableFrom(DisabledException.class)) {
                redirectAttributes.addFlashAttribute("errorMessages", "Tài khoản chưa được xác thực, vui lòng nhập email bên dưới để xác thực tài khoản");
                return "redirect:/verification_new_account";
            }
            model.addAttribute("errorMessages", loginException.getMessage());
        }
        return "login";
    }
    
    @RequestMapping(value="/login_admin")
    public String loginAdmin(@RequestParam(value = "error", required = false) boolean loginError, HttpServletRequest request, Model model) {
        LOGGER.info("Login: " + request.getClass().getName());
        return "loginAdmin";
    }
    
    
    @RequestMapping(value="/register", method=RequestMethod.GET)
    public String register(HttpServletRequest request, Model model) {
        LOGGER.info("register");
        Enumeration enums = request.getSession().getAttributeNames();
        while(enums.hasMoreElements()) {
            LOGGER.info("Session attribute name: " + enums.nextElement());
        }
        UserDto userDto = new UserDto();
        model.addAttribute("userDto", userDto);
        model.addAttribute("myname", "riot");
        return "register";
    }
    
    
    @RequestMapping(value="/register", method=RequestMethod.POST)
    public String processRegistration(
            @ModelAttribute("userDto") @Valid UserDto user, 
            BindingResult bindingResult, 
            HttpServletRequest request, 
            Model model, RedirectAttributes redirectAttributes) {
        UserAccount registed = null;
        LOGGER.info("registration process");
        LOGGER.info(String.valueOf(model.containsAttribute("userDto")));
        LOGGER.info(String.valueOf(model.containsAttribute("org.springframework.validation.BindingResult.userDto")));
        
        Enumeration enums = request.getSession().getAttributeNames();
        while(enums.hasMoreElements()) {
            LOGGER.info("Session attribute name: " + enums.nextElement());
        }
        Iterator iterator = bindingResult.getModel().keySet().iterator();
        while(iterator.hasNext()) {
            LOGGER.info(iterator.next().toString());
        }
        
        iterator = model.asMap().keySet().iterator();
        while(iterator.hasNext()) {
            LOGGER.info("model -> map key set: " + iterator.next().toString());
        }
        
        if(bindingResult.hasErrors()) {
            LOGGER.info("registration failed");
            return "register";
        }
        
        registed = createUserAccount(user, bindingResult);
        if(registed == null) {
            for(ObjectError objectError : bindingResult.getAllErrors()) {
                System.out.println(objectError.getObjectName());
            }

            LOGGER.info("registration failed");
            return "register";
        }
        
        try {
            ApplicationEvent event = new OnRegistrationCompleteEvent(request.getContextPath(), registed, request.getLocale());
            applicationEventPublisher.publishEvent(event);
        } catch(Exception ex) {
            LOGGER.info("send verification email error: " + ex.getMessage());
            bindingResult.reject("email", "Email not found");
            return "register";
        }
        redirectAttributes.addFlashAttribute("successMessages", "Đăng ký tài khoản thành công! Vui lòng kiểm tra email và làm theo hướng dẫn để xác thực tài khoản");
        return "redirect:/login";
    }
    
    private UserAccount createUserAccount(UserDto userDto, BindingResult bindingResult) {
        UserAccount userAccount = null;
        try {
            userAccount = userService.registerNewUserAccount(userDto);
        } catch(EmailExistsException emailExistsException) {
            emailExistsException.printStackTrace();
//            bindingResult.reject("email");
        
            bindingResult.rejectValue("email", "", emailExistsException.getMessage());
//            bindingResult.rejectValue("email", "email");
        } catch(AccountExistsException accountExistsException) {
            accountExistsException.printStackTrace();
            bindingResult.rejectValue("email", "", accountExistsException.getMessage());
        }
        
        return userAccount;
    }
    
    @RequestMapping(value="/registrationConfirm", method=RequestMethod.GET)
    public String confirmRegistration(@RequestParam("token") String token, Model model) {
        VerificationToken verificationToken = verificationTokenRepository.findByToken(token);
        
        if(verificationToken == null) {
            String message = "Bad registration confirm info";
            model.addAttribute("message", message);
            return "newAccountVerification";
        }
        
        Calendar cal = Calendar.getInstance();
        if(verificationToken.getExpiryDate().getTime() - cal.getTime().getTime() <= 0) {
            String message = "Mã xác thực đã hết hạn !";
            model.addAttribute("message", message);
            model.addAttribute("expired", true);
            model.addAttribute("token", token);
            return "newAccountVerification";
        }
        
        UserAccount userAccount = verificationToken.getUserAccount();
        userAccount.setEnable(true);
        userAccountRepository.update(userAccount);
        model.addAttribute("message", "Xác thực tài khoản thành công");
        return "newAccountVerification";
    }
    
    @RequestMapping(value="/login-google", method=RequestMethod.GET)
    public String processLoginGoogle(HttpServletRequest request, Model model, RedirectAttributes redirectAttributes) {
        String code = request.getParameter("code");
        if(!(code != null && !code.isEmpty())) {
            redirectAttributes.addFlashAttribute("errorMessages", "Đăng nhập thất bại");
            return "redirect:/login?error=true";
        }
        String accessToken = null;
        GooglePojo googlePojo = null;
        LOGGER.info("Google code: " + code);
        try {
            accessToken = googleUtils.getToken(code);
            if(!(accessToken != null && !accessToken.isEmpty())) {
                throw new NullPointerException("AccessToken is NULL");
            }
            googlePojo = googleUtils.getUserInfo(accessToken);
        } catch(ClientProtocolException googleRequestException) {
            googleRequestException.printStackTrace();
        } catch(IOException ex) {
            ex.printStackTrace();
        }
        if(googlePojo == null) {
            LOGGER.error("Google login: " + "Some exception occured when get token or user info from Google! Google POJO is NULL");
            redirectAttributes.addFlashAttribute("errorMessages", "Đăng nhập thất bại");
            return "redirect:/login?error=true";
        }
        User user = userRepository.findByEmail(googlePojo.getEmail());
        if(user == null) {
            LOGGER.info("Google login: " + "No account with current gmail! Auto create new account");
            try {
                user = googleUtils.createNewUser(googlePojo).getUser();
            } catch(EmailExistsException emailExistsException) {
                emailExistsException.printStackTrace();
            } catch(AccountExistsException accountExistsException) {
                accountExistsException.printStackTrace();
            }
        }
        UserAccount userAccount = user.getUserAccount();
        UserDetails userDetail = securityService.buildUser(userAccount);
        UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(userDetail, null, userDetail.getAuthorities());
        SecurityContextHolder.getContext().setAuthentication(authentication);
        return "redirect:/";
    }
    
    @RequestMapping(value="/login-with-google")
    public String loginGoogle() {
        String googleAuthLink = "https://accounts.google.com/o/oauth2/auth?"
                + "scope=https://www.googleapis.com/auth/userinfo.profile https://www.googleapis.com/auth/userinfo.email"
                + "&redirect_uri=http://localhost:8080/BookShareWeb/login-google"
                + "&response_type=code"
                + "&client_id=924849780106-r6pg7f84voodmhnej6a2u8q3o3dgsgev.apps.googleusercontent.com"
                + "&approval_prompt=force";
        return "redirect:" + googleAuthLink;
    }
    
    
    @RequestMapping("/resendRegistrationToken")
    public String resendRegistrationToken(HttpServletRequest request, @RequestParam("token") String token, Model model) {
        VerificationToken verificationToken = userService.generateNewVerificationToken(token);
        
        if(verificationToken == null) {
            String message = "Thông tin xác thực không chính xác";
            model.addAttribute("message", message);
            return "newAccountVerification";
        }
        
        UserAccount userAccount = verificationToken.getUserAccount();
        
        if(userAccount == null) {
            String message = "Thông tin xác thực không chính xác";
            model.addAttribute("message", message);
            return "newAccountVerification";
        }  
        
        String appUrl = "http://" + request.getServerName() + ":" + request.getServerPort() + request.getContextPath();
        String newToken = verificationToken.getToken();
        
        String recipientAddress = userAccount.getUser().getEmail();
        String subject = "Registration Confirmation";
        String confirmationUrl = appUrl + "/registrationConfirm?token=" + newToken;
        
        String message = "Xác nhận tài khoản Chia Sẻ Sách tại: ";
        SimpleMailMessage email = new SimpleMailMessage();
        email.setSubject(subject);
        email.setText(message + confirmationUrl);
        email.setTo(recipientAddress);
        mailSender.send(email);
        
        model.addAttribute("message", "Yêu cầu xác thực lại thành công. Vui lòng kiểm tra email"
                + " và làm theo hướng dẫn để xác thực lại tài khoản");
        
        return "newAccountVerification";
    }

}
