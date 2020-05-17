/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.khai.bookshareweb.data;

import com.khai.bookshareweb.config.WebConfig;
import com.khai.bookshareweb.dto.BookDTO;
import com.khai.bookshareweb.entity.Book;
import com.khai.bookshareweb.entity.Type;
import com.khai.bookshareweb.entity.User;
import com.khai.bookshareweb.interceptor.LoggedUserInterceptor;
import com.khai.bookshareweb.service.BookService;
import com.khai.bookshareweb.service.SecurityService;
import java.util.List;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.servlet.ServletContext;
import javax.servlet.http.HttpSession;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpSession;
import org.springframework.mock.web.MockServletContext;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;


import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 *
 * @author Acer
 */
//@RunWith(SpringJUnit4ClassRunner.class)
//@ContextConfiguration(classes = { HibernateBookRepositoryConfig.class, TestConfig.class})
//@ContextConfiguration(classes = { HibernateBookRepositoryConfig.class })
//@ContextConfiguration(classes = { TestConfig.class })
//@WebAppConfiguration 
public class UserControllerUnitTest {
    
    private MockMvc mockMvc;
    
    @Autowired
    private WebApplicationContext wac;
    
    @Autowired
    private BookRepository bookRepository;
    
    @Autowired
    private BookService bookService;
    
//    @Autowired
    private SecurityService securityService;

    
//    @Before
    public void setup() {
        Mockito.reset(bookService);
        this.mockMvc = MockMvcBuilders.webAppContextSetup(this.wac).build();
    }
    
    @Test
    public void defaultTest() {
    
    }
    
//    @Test
    public void testGetBean() {
        ServletContext servletContext = wac.getServletContext();
        Assert.assertNotNull(servletContext);
        Assert.assertTrue(servletContext instanceof MockServletContext);
        Assert.assertNotNull(wac.getBean("bookController"));
    }
    
//    @Test
    public void findBookWithId_ShouldFindBookWithSpecificIdAndRenderView() throws Exception {
        Book book = new Book();
        book.addType(new Type(1, ""));
        Mockito.when(bookRepository.findOne(10)).thenReturn(book);
        
        mockMvc.perform(MockMvcRequestBuilders.get("/book/10/info"))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.view().name("book"))
                .andExpect(MockMvcResultMatchers.model().attributeExists("book"));
        
        
    }
    
//    @Test
    public void pageNotFound_ShouldReturnPageNotFoundTemplate() throws Exception {
        mockMvc.perform(get("/unknowPage"))
                .andExpect(status().isNotFound())
                .andExpect(view().name("errors/404"));
    }
    
//    @Test
    public void testSearchBookWithPaginationAndOrderRestApi_ShoudReturnAnJSONContainOrderedListOfBookDTOs() throws Exception {
        Properties searchProperties = new Properties();
        searchProperties.setProperty("searchKey", "java");
        searchProperties.setProperty("orderBy", "view");
        searchProperties.setProperty("page", "1");
        searchProperties.setProperty("maxResult", String.valueOf(20));
        searchProperties.setProperty("firstResultIndex", String.valueOf(0));
        
        List<BookDTO> bookDTOs = bookRepository.searchBookWithSearchPropertiesAndOrderBy(searchProperties, BookRepository.BooksSelectedOrderBy.BOOK_POSTED_DATE);
        
        System.out.println(bookDTOs.size());
        
        searchProperties.remove("firstResultIndex");
        
        Mockito.when(bookService.searchBooks(searchProperties)).thenReturn(bookDTOs);

        String responseContent = mockMvc.perform(get("/api/v1/book/search/results?search_key=java&page=1&orderBy=view"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8))
            .andReturn().getResponse().getContentAsString();
        
        System.out.println(responseContent);
    
    }
    
//    @Test
    public void testSearchUserBooksPosted_ShoudReturnOKStatus() throws Exception {
        Properties searchProperties = new Properties();
        searchProperties.setProperty("searchKey", "java");
        searchProperties.setProperty("orderBy", "view");
        searchProperties.setProperty("page", "1");
        searchProperties.setProperty("maxResult", String.valueOf(20));
        searchProperties.setProperty("firstResultIndex", String.valueOf(0));
        
        User user = new User(42, null, null, null);
        
        List<BookDTO> bookDTOs = bookRepository.searchBookWithSearchPropertiesAndOrderBy(
                searchProperties,
                BookRepository.BooksSelectedOrderBy.BOOK_POSTED_DATE,
                user.getUserId());
        
        System.out.println(bookDTOs.size());
        
        MockHttpSession mockHttpSession = new MockHttpSession();
        
        searchProperties.remove("firstResultIndex");
        
        Mockito.when(securityService.getCurrentLoggedUserInSession(mockHttpSession)).thenReturn(user);
        Mockito.when(bookService.searchBooks(searchProperties)).thenReturn(bookDTOs);

        String responseContent = mockMvc.perform(get("/user/book-posted/results?search_key=java").session(mockHttpSession))
            .andExpect(status().isOk())
            .andExpect(view().name("user/booksPosted"))
            .andReturn().getResponse().getContentAsString();
        
        System.out.println(responseContent);
    
    }
    
//    @Test
    public void testSearchBookRestApi_ShoudReturnAnJSONContainListOfBookDTOs() throws Exception {
        List<BookDTO> bookDTOs = bookRepository.searchBookWithLimit(5, "java");
        Mockito.when(bookService.searchBooks(5, "java")).thenReturn(bookDTOs);

        String responseContent = mockMvc.perform(get("/api/v1/book/searchBook?search_key=java"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8))
            .andReturn().getResponse().getContentAsString();
        
        System.out.println(responseContent);
    
    }
    
//    @Test
    public void testSearchBookRestApi_ShoudReturn406StatusAndErrorMessage() throws Exception {
        List<BookDTO> bookDTOs = bookRepository.searchBookWithLimit(5, "java");
        Mockito.when(bookService.searchBooks(5, "java")).thenReturn(bookDTOs);

        String responseContent = mockMvc.perform(get("/api/v1/book/searchBook"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8))
            .andReturn().getResponse().getContentAsString();
        
        System.out.println(responseContent);
    
    }
    
//    @Test
    public void testBookApproval_ShouldUpdateBookWithApporvedStatusCodeAndReturnBookWithNewUpdate() throws Exception {
        int bookId = 96;
        Book book = bookRepository.findOne(bookId);
        book.setStatusCode(HibernateBookRepository.BOOK_APPROVED_STATUS_CODE);
        Mockito.when(bookService.getBookById(bookId)).thenReturn(book);
        Mockito.when(bookService.bookApproval(book)).thenReturn(book);
        
        mockMvc.perform(post("/admin/book-approval")
            .param("bookId", String.valueOf(bookId)))
            .andExpect(status().is3xxRedirection())
            .andExpect(flash().attributeExists("handleSuccess"));
        
    }
    
}
