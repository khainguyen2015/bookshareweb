/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.khai.bookshareweb.dto;

import com.khai.bookshareweb.entity.BookImage;
import java.util.List;
import org.springframework.web.multipart.MultipartFile;

/**
 *
 * @author Acer
 */
public class BookUpdateDTO extends BookCreationDTO {
     
    private int[] imageRemovedIds;
    
    private int[] imageUpdateIds;
    
    private MultipartFile[] bookUpdateImagesMultipartFile;

    public int[] getImageRemovedIds() {
        return imageRemovedIds;
    }

    public void setImageRemovedIds(int[] imageRemovedIds) {
        this.imageRemovedIds = imageRemovedIds;
    }

    public int[] getImageUpdateIds() {
        return imageUpdateIds;
    }

    public void setImageUpdateIds(int[] imageUpdateIds) {
        this.imageUpdateIds = imageUpdateIds;
    }

    public MultipartFile[] getBookUpdateImagesMultipartFile() {
        return bookUpdateImagesMultipartFile;
    }

    public void setBookUpdateImagesMultipartFile(MultipartFile[] bookUpdateImagesMultipartFile) {
        this.bookUpdateImagesMultipartFile = bookUpdateImagesMultipartFile;
    }
    
    
}
