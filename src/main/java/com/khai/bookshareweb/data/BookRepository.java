/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.khai.bookshareweb.data;

import com.khai.bookshareweb.dto.BookDTO;
import com.khai.bookshareweb.entity.Book;
import java.util.List;
import java.util.Properties;


/**
 *
 * @author Acer
 */
public interface BookRepository {
    
    enum BooksSelectedOrderBy {
        BOOK_VIEW,
        BOOK_POSTED_DATE
    }
    
    public long count();
    
    public Book save(Book book);
    
    public Book add(Book book);
    
    public void remove(Book book);
    
    public Book findAnApprovedBook(int id);
    
    public Book findOne(int id);
    
    public Book findByBookName(String bookName);
    
    public List<Book> findAll();
   
    public List<Book> findByUserId(int userId);
    
    public List<BookDTO> findAll_LiteVersion();
    
    public List<BookDTO> findAll_LiteVersion(int maxResult, int firstResultIndex);
    
    public List<BookDTO> findAll_LiteVersion(int maxResult, int firstResultIndex, int userId);
    
    public List<BookDTO> findAll_LiteVersion(Properties propertiesForFind, BooksSelectedOrderBy orderBy, int userId);
    
    public List<BookDTO> findAllBooksNotAprrove(int maxResult, int firstResultIndex);
    
    public List<BookDTO> searchBookWithLimit(int max, String searchKey);
    
    public List<BookDTO> searchBookWithLimit(int max, String searchKey, int userId);
    
    public List<BookDTO> searchBookWithSearchPropertiesAndOrderBy(Properties searchProperties, BooksSelectedOrderBy orderBy);
    
    public List<BookDTO> searchBookWithSearchPropertiesAndOrderBy(Properties searchProperties, BooksSelectedOrderBy orderBy, int userId);
    
    public long getAmountOfBooks();
    
    public long getAmountOfBooks(int userId);
    
    public long getAmountOfBooks(String searchKey);
    
    public long getAmountOfBooks(String searchKey, int userId);
    
    public boolean isBookExist(int bookId);
}
