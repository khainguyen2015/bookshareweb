/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.khai.bookshareweb.web.rest;

import com.khai.bookshareweb.dto.BookDTO;
import com.khai.bookshareweb.service.BookService;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 *
 * @author Acer
 */

@RestController
@RequestMapping("/api/v1/book/*")
public class BookRestController {
    
    private static final int DEFAULT_AMOUNT_SEARCH_RESULT = 5;
    
    @Autowired
    private BookService bookService;
    
    @GetMapping(value = "/searchBook")
    public ResponseEntity<Object> searchBook(@RequestParam(name = "amount_of_results", required = false) Integer amountOfResult,
            @RequestParam(name = "search_key", required = false) String searchKey) {
        if(amountOfResult == null) {
            amountOfResult = DEFAULT_AMOUNT_SEARCH_RESULT;
        }
        List<BookDTO> searchResults = new ArrayList<>();
        if(searchKey != null && !searchKey.isEmpty()) {
            searchResults = bookService.searchBooks(amountOfResult, searchKey);
        }
        return new ResponseEntity(searchResults, HttpStatus.OK);
    }
    
    @GetMapping(value="/search/results")
    public ResponseEntity<Object> searchBook(
            @RequestParam(value = "search_key", required = false) String searchKey,
            @RequestParam(value = "page", required = false) Integer page,
            @RequestParam(value = "orderBy", required = false) String orderBy) {
        
        List<BookDTO> bookDTOs = new ArrayList<>();
        
        int currentPage = 1;
  
        if(searchKey == null ) {
            return new ResponseEntity(bookDTOs, HttpStatus.OK);
        }
        
        if(searchKey.isEmpty()) {
            return new ResponseEntity(bookDTOs, HttpStatus.OK);
        }
        
        if(page != null) {
            currentPage = page;
        }
        
        if(orderBy == null) {
            orderBy = "view";
        }
        
        if(orderBy.isEmpty()) {
            orderBy = "view";
        }
        
        Properties searchProperties = new Properties();
        searchProperties.setProperty("searchKey", searchKey);
        searchProperties.setProperty("orderBy", orderBy);
        searchProperties.setProperty("page", String.valueOf(page));
        searchProperties.setProperty("maxResult", String.valueOf(20));
          
        System.out.println("dawdaw");
        
        bookDTOs = bookService.searchBooks(searchProperties);
        return new ResponseEntity(bookDTOs, HttpStatus.OK);
    }
    
}
