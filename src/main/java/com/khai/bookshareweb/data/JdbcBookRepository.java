/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.khai.bookshareweb.data;

import com.khai.bookshareweb.dto.BookDTO;
import com.khai.bookshareweb.entity.Book;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.Properties;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcOperations;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

/**
 *
 * @author Acer
 */
//@Repository
public class JdbcBookRepository {
    
    @Autowired
    public JdbcOperations jdbcOperations;
    

    public List<Book> findAll() {
        List<String> list = jdbcOperations.queryForList("SELECT book_name FROM book", null, null, String.class);
        for(String s : list) System.out.println(s);
        return jdbcOperations.query("SELECT * FROM book", null, null, new BookRowMapper());
        
         
    }
    

    public long count() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
    

    public Book add(Book book) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }


    public Book save(Book book) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }


    public Book findOne(int id) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }


    public Book findByBookName(String bookName) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
    

    public List<Book> findByUserId(int userId) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
    

    public List<BookDTO> findAll_LiteVersion() {
        throw new UnsupportedOperationException("Not supported yet.");
    }
    

    public List<BookDTO> findByUserId_LiteVersion(int userId) {
        throw new UnsupportedOperationException("Not supported yet.");
    }
    

    public void remove(Book book) {
        throw new UnsupportedOperationException("Not supported yet.");
    }
    
    
    public List<BookDTO> searchBookWithLimit(int max, String searchKey) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    
    public List<BookDTO> searchBookWithLimit(int max, String searchKey, int userId) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    
    public List<BookDTO> findAll_LiteVersion(int maxResult, int firstResultIndex) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    
    public long getAmountOfBooks() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    
    public long getAmountOfBooks(int userId) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    
    private static final class BookRowMapper implements RowMapper<Book> {
        public Book mapRow(ResultSet rs, int rowNum) throws SQLException {
            return new Book(
                    rs.getInt("book_id"),
                    rs.getString("book_name"),
                    rs.getString("author"),
                    rs.getString("book_description"),
                    rs.getDate("post_date"),
                    rs.getInt("book_view"),
                    rs.getString("thumbnail_url"),
                    null
            );
        }
    }
    
}
