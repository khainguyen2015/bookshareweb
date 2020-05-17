/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.khai.bookshareweb.dto;

import com.khai.bookshareweb.entity.BookDownloadLink;
import com.khai.bookshareweb.entity.BookImage;
import com.khai.bookshareweb.entity.User;
import java.util.ArrayList;
import java.util.List;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.DecimalMin;

import org.springframework.web.multipart.MultipartFile;

/**
 *
 * @author Acer
 */
public class BookCreationDTO {
    
    private int bookId;
    
    @NotNull
    @DecimalMin(value="1", message="Thể loại sách không hợp lệ")
    private int bookType;
    
    @NotNull
    @Pattern(regexp="[A-Za-z0-9 ,.()\"\"''?:-]{5,200}", message="Tên sách không hợp lệ")
    private String bookName;
    
    @NotNull
    @Pattern(regexp="[A-Za-z0-9 ,.()\"\"''?:-]{5,100}", message="Tên tác giả không hợp lệ")
    private String authorName;
    
    private String description;
    
    private String bookThumbnailImageUrl;
    
    private User user;

    @NotNull(message="Ảnh bìa không hợp lệ")
    private MultipartFile bookThumbnailImage;
    
    private List<BookDownloadLink> bookDownloadLinks = new ArrayList<>();
    
    private MultipartFile[] bookImagesMultipartFile;
    
    private List<BookImage> bookImages = new ArrayList<>();
    
    public List<BookImage> getBookImages() {
        return bookImages;
    }

    public void setBookImages(List<BookImage> bookImages) {
        this.bookImages = bookImages;
    }
    
    public void addBookImages(BookImage bookImage) {
        this.bookImages.add(bookImage);
    }

    public int getBookType() {
        return bookType;
    }

    public void setBookType(int bookType) {
        this.bookType = bookType;
    }

    public String getBookName() {
        return bookName;
    }
    
    public void setBookName(String bookName) {
        this.bookName = bookName;
    }

    public String getAuthorName() {
        return authorName;
    }

    public void setAuthorName(String authorName) {
        this.authorName = authorName;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public MultipartFile[] getBookImagesMultipartFile() {
        return bookImagesMultipartFile;
    }

    public void setBookImagesMultipartFile(MultipartFile[] bookImagesMultipartFile) {
        this.bookImagesMultipartFile = bookImagesMultipartFile;
    }

    public List<BookDownloadLink> getBookDownloadLinks() {
        return bookDownloadLinks;
    }

    public void setBookDownloadLinks(List<BookDownloadLink> BookDownloadLinks) {
        this.bookDownloadLinks = BookDownloadLinks;
    }
    
    public void addDownloadLink(BookDownloadLink BookDownloadLink) {
        bookDownloadLinks.add(BookDownloadLink);
    }

    public MultipartFile getBookThumbnailImage() {
        return bookThumbnailImage;
    }

    public void setBookThumbnailImage(MultipartFile bookThumbnailImage) {
        this.bookThumbnailImage = bookThumbnailImage;
    }

    public int getBookId() {
        return bookId;
    }

    public void setBookId(int bookId) {
        this.bookId = bookId;
    }

    public String getBookThumbnailImageUrl() {
        return bookThumbnailImageUrl;
    }

    public void setBookThumbnailImageUrl(String bookThumbnailImageUrl) {
        this.bookThumbnailImageUrl = bookThumbnailImageUrl;
    }      
    
}
