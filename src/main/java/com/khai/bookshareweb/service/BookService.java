/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.khai.bookshareweb.service;

import com.khai.bookshareweb.dto.BookCreationDTO;
import com.khai.bookshareweb.dto.BookDTO;
import com.khai.bookshareweb.dto.BookUpdateDTO;
import com.khai.bookshareweb.entity.Book;
import com.khai.bookshareweb.entity.BookDownloadLink;
import com.khai.bookshareweb.entity.User;
import com.khai.bookshareweb.service.exception.TypeNotFoundException;
import com.khai.bookshareweb.service.exception.UserIdNotMatchesException;
import java.util.List;
import java.util.Properties;
import org.springframework.web.multipart.MultipartFile;

/**
 *
 * @author Acer
 */
public interface BookService {
    
    public Book createNewBook(BookCreationDTO bookCreationDTO) throws TypeNotFoundException;
    
    public boolean checkBookDownloadLink(BookDownloadLink bookDownloadLink);
    
    public void deleteBookById(int bookId, int userId) throws UserIdNotMatchesException;
    
    public Book updateBook(Book book, BookUpdateDTO BookUpdateDTO) throws TypeNotFoundException;
    
    public Book getBookById(int bookId);
    
    public List<BookDTO> searchBooks(int max, String searchKey);
    
    public List<BookDTO> searchBooks(Properties searchProperties);
    
    public List<BookDTO> searchBooks(Properties searchProperties, int userId);
    
    public List<BookDTO> pagination(int amountOfBooksPerPage, int currentPage);
    
    public List<BookDTO> pagination(int amountOfBooksPerPage, int currentPage, int userId);
    
    public List<BookDTO> getAllBookNotApproveWithPagination(int amountOfBooksPerPage, int currentPage);
    
    /**
     * Select books posted of specific user with pagination
     * <p>Note: pageProperties are properties need for pagination. It's include
     * "orderBy": property for order the result list. Default is "view".
     * "page": property show the current page number in pagination.
     * "maxResults": property show amount of books per page in pagination.
     */
    public List<BookDTO> pagination(Properties pageProperties, int userId);
    
    public int amountOfBooks();
    
    public int amountOfBooks(int userId);
    
    public int amountsOfSearchedResults(String searchKey);
    
    public int amountsOfSearchedResults(String searchKey, int userId);
    
    public BookCreationDTO getBookCreationDTO();
    
    public BookUpdateDTO getBookUpdateDTO(Book book);
    
    public BookUpdateDTO handleBookUpdateDTOAfterValidFalse(BookUpdateDTO bookUpdateDTO, Book book);
    
    public BookCreationDTO handleBookCreationDTOAfterValidFalse(BookCreationDTO bookCreationDTO);
    
    public boolean isBookApproved(Book book);
            
    public Book bookApproval(Book book);
    
}
