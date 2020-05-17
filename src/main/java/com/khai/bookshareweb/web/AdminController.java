/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.khai.bookshareweb.web;

import com.khai.bookshareweb.dto.BookDTO;
import com.khai.bookshareweb.entity.Book;
import com.khai.bookshareweb.entity.User;
import com.khai.bookshareweb.service.BookService;
import com.khai.bookshareweb.service.SecurityService;
import com.khai.bookshareweb.service.exception.BookNotFoundException;
import java.util.List;
import javax.servlet.http.HttpSession;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

/**
 *
 * @author Acer
 */

@Controller
@RequestMapping(value = "/admin/*")
public class AdminController {
    private static final Log LOGGER = LogFactory.getLog(AdminController.class);
    
    private static final int BOOKS_PER_PAGE = 20;
    
    @Autowired
    private BookService bookService;
    
    @Autowired
    private SecurityService sercurityService;
    
    @RequestMapping(value = "/book-approval")
    public String bookApproval(Model model, @RequestParam(name = "page", required = false) Integer page) {
        int currentPage = 1;
        if(page != null && page > 0) {
            currentPage = page;
        }
        List<BookDTO> bookNotApproveList = bookService.getAllBookNotApproveWithPagination(BOOKS_PER_PAGE, currentPage);
        model.addAttribute("books", bookNotApproveList);
        prepareAttributeForPagination(model, currentPage, BOOKS_PER_PAGE);
        return "/admin/bookApproval";
    }
    
    @RequestMapping(value = "/book-approval-handle", method = RequestMethod.POST)
    public String bookApprovalHanlder(
            RedirectAttributes redirectAttributes,
            @RequestParam(name = "bookId")Integer bookId,
            HttpSession session) {
        if(bookId == null) {
            return "redirect:/admin/book-approval";
        }
        Book book = bookService.getBookById(bookId);
        if(book == null) {
            return "redirect:/admin/book-approval";
        }
        book = bookService.bookApproval(book);
        redirectAttributes.addFlashAttribute("handleSuccess", true);
        return "redirect:/admin/book-approval";
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
    
}
