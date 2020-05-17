/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.khai.bookshareweb.service;

import org.springframework.web.multipart.MultipartFile;

/**
 *
 * @author Acer
 */
public interface MultipartFileService {
    
    public String storeImageToSpecificPath(MultipartFile multipartFile, String path);
    
    public String storeBookCoverImage(MultipartFile file);
    
    public String storeTempBookCoverImage(MultipartFile file);
    
    public String storeBookImage(MultipartFile file);
    
    public String storeTempBookImage(MultipartFile file);
    
    public String moveBookCoverImageFromTempStorageToMainStorage(String fileName);
    
    public String moveBookImageFromTempStorageToMainStorage(String fileName);
    
    public void removeBookImage(String fileName);
    
    public void removeBookCoverImage(String fileName);
    
    public void removeTempBookImage(String fileName);
    
    public void removeTempBookCoverImage(String fileName);

}
