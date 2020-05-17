/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.khai.bookshareweb.dto;

import java.util.Calendar;
import java.util.Date;

/**
 *
 * @author Acer
 */
public class BookDTO {
    
    private int bookId;
    private int userId;
    private int bookView;
    private int statusCode;
    private String bookName;
    private String author;
    private String thumbnailUrl;
    private String meaningfulPostDate;
    private String userName;
    private Date postDate;
    public BookDTO() {
        
    }
     
    public BookDTO(int bookId, String bookName, String author, int bookView, String thumbnailUrl, int userId) {
        this.bookId = bookId;
        this.bookName = bookName;
        this.author = author;
        this.bookView = bookView;
        this.thumbnailUrl = thumbnailUrl;
        this.userId = userId;
    }
    
    public BookDTO(int bookId, String bookName, String author, int bookView, String thumbnailUrl, int userId, Date postDate) {
        this.bookId = bookId;
        this.bookName = bookName;
        this.author = author;
        this.bookView = bookView;
        this.thumbnailUrl = thumbnailUrl;
        this.userId = userId;
        this.postDate = postDate;
    }
    
    public BookDTO(int bookId, String bookName, String author, int bookView, String thumbnailUrl, int userId, Date postDate, int statusCode) {
        this.bookId = bookId;
        this.bookName = bookName;
        this.author = author;
        this.bookView = bookView;
        this.thumbnailUrl = thumbnailUrl;
        this.userId = userId;
        this.postDate = postDate;
        this.statusCode = statusCode;
    }
    
    public BookDTO(int bookId, String bookName, String author, int bookView, String thumbnailUrl, int userId, Date postDate, int statusCode, String userName) {
        this.bookId = bookId;
        this.bookName = bookName;
        this.author = author;
        this.bookView = bookView;
        this.thumbnailUrl = thumbnailUrl;
        this.userId = userId;
        this.postDate = postDate;
        this.statusCode = statusCode;
        this.userName = userName;
    }

    public int getBookId() {
        return bookId;
    }

    public String getBookName() {
        return bookName;
    }

    public String getAuthor() {
        return author;
    }

    public int getBookView() {
        return bookView;
    }

    public String getThumbnailUrl() {
        return thumbnailUrl;
    }

    public int getUserId() {
        return userId;
    }

    public Date getPostDate() {
        return postDate;
    }

    public String getMeaningfulPostDate() {
        return meaningfulPostDate;
    }

    public void setMeaningfulPostDate(String meaningfulPostDate) {
        this.meaningfulPostDate = meaningfulPostDate;
    }

    public int getStatusCode() {
        return statusCode;
    }

    public void setStatusCode(int statusCode) {
        this.statusCode = statusCode;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

}
