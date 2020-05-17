/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.khai.bookshareweb.dto;

import org.springframework.web.multipart.MultipartFile;

/**
 *
 * @author Acer
 */
public class BookImageDTO {
    private int id;
    
    private String imageURL;
    
    private MultipartFile imageMultipartFile;
    
    public BookImageDTO() {
        
    }

    public BookImageDTO(int id, String imageURL) {
        this.id = id;
        this.imageURL = imageURL;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getImageURL() {
        return imageURL;
    }

    public void setImageURL(String imageURL) {
        this.imageURL = imageURL;
    }

    public MultipartFile getImageMultipartFile() {
        return imageMultipartFile;
    }

    public void setImageMultipartFile(MultipartFile imageMultipartFile) {
        this.imageMultipartFile = imageMultipartFile;
    }
    
    
}
