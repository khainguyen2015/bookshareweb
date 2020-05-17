/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.khai.bookshareweb.service;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;
import javax.servlet.ServletContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

/**
 *
 * @author Acer
 */
@Service
public class MultipartFileServiceImpl implements MultipartFileService {
    
    private static final String FILE_SEPARATOR = System.getProperty("file.separator");
    
    private static final List<String> VALID_IMAGE_TYPES = Arrays.asList("jpeg", "png", "jpg", "x-png");
    
    private static final String BOOK_COVER_IMAGES_STORAGE_PATH = "public" + FILE_SEPARATOR + "images" + FILE_SEPARATOR + "thumbnails";
       
    private static final String BOOK_IMAGES_STORAGE_PATH = "public" + FILE_SEPARATOR + "images" + FILE_SEPARATOR + "bookImages";
   
    private static final String TEMP_BOOK_IMAGES_STORAGE_PATH = "public" + FILE_SEPARATOR + "images" + FILE_SEPARATOR + "tempBookImages";
    
    
    @Autowired
    private ServletContext servletContext;
    
    @Override
    public String storeImageToSpecificPath(MultipartFile image, String path) {
        String fileName = "";
        try {
            String fileType = getFileType(image);
            if(fileType.isEmpty()) return "";
            if(!VALID_IMAGE_TYPES.contains(fileType)) {
                System.out.println("Wrong image type: " + image.getOriginalFilename());
                return "";
            }
            fileName = getNewFileName(fileType);
            path = path + FILE_SEPARATOR + fileName;
            image.transferTo(new File(path));
        } catch(Exception ex) {
            ex.printStackTrace();
            return "";
        }
        System.out.println(image.getOriginalFilename());
        System.out.println(path);
        return fileName;
    }
    
    @Override
    public String storeBookCoverImage(MultipartFile file) {
        if(file.isEmpty()) {
            return "";
        }
        String pathToStore = servletContext.getRealPath(BOOK_COVER_IMAGES_STORAGE_PATH);
        String fileName = storeImageToSpecificPath(file, pathToStore);
        return fileName;
    }
    
    @Override
    public String storeTempBookCoverImage(MultipartFile file) {
        if(file.isEmpty()) {
            return "";
        }
        String pathToStore = servletContext.getRealPath(TEMP_BOOK_IMAGES_STORAGE_PATH);
        String fileName = storeImageToSpecificPath(file, pathToStore);
        return fileName;
    }
    
    @Override
    public String storeBookImage(MultipartFile file) {
        if(file.isEmpty()) {
            return "";
        }
        String pathToStore = servletContext.getRealPath(BOOK_IMAGES_STORAGE_PATH);
        String fileName = storeImageToSpecificPath(file, pathToStore);
        return fileName;
    }
    
    @Override
    public String storeTempBookImage(MultipartFile file) {
        if(file.isEmpty()) {
            return "";
        }
        String pathToStore = servletContext.getRealPath(TEMP_BOOK_IMAGES_STORAGE_PATH);
        String fileName = storeImageToSpecificPath(file, pathToStore);
        return fileName;
    }
    
    @Override
    public String moveBookCoverImageFromTempStorageToMainStorage(String fileName) {
        if(fileName.isEmpty()) return "";
        String tempBookCoverStoragePath = servletContext.getRealPath(TEMP_BOOK_IMAGES_STORAGE_PATH);
        String bookCoverStoragePath = servletContext.getRealPath(BOOK_COVER_IMAGES_STORAGE_PATH);
        return moveBookImageFromTempStorageToMainStorage(fileName, tempBookCoverStoragePath, bookCoverStoragePath);
    }
    
    @Override
    public String moveBookImageFromTempStorageToMainStorage(String fileName) {
        if(fileName.isEmpty()) return "";
        String tempBookStoragePath = servletContext.getRealPath(TEMP_BOOK_IMAGES_STORAGE_PATH);
        String bookStoragePath = servletContext.getRealPath(BOOK_IMAGES_STORAGE_PATH);
        return moveBookImageFromTempStorageToMainStorage(fileName, tempBookStoragePath, bookStoragePath);
    }
    
    @Override
    public void removeTempBookCoverImage(String fileName) {
        String imageStoragePath = servletContext.getRealPath(TEMP_BOOK_IMAGES_STORAGE_PATH);
        removeImage(imageStoragePath + FILE_SEPARATOR + fileName);
    }
    
    @Override
    public void removeTempBookImage(String fileName) {
        String imageStoragePath = servletContext.getRealPath(TEMP_BOOK_IMAGES_STORAGE_PATH);
        removeImage(imageStoragePath + FILE_SEPARATOR + fileName);
    }

    @Override
    public void removeBookImage(String fileName) {
        String imageStoragePath = servletContext.getRealPath(BOOK_IMAGES_STORAGE_PATH);
        removeImage(imageStoragePath + FILE_SEPARATOR + fileName);
    }
    
    @Override
    public void removeBookCoverImage(String fileName) {
        String imageStoragePath = servletContext.getRealPath(BOOK_COVER_IMAGES_STORAGE_PATH);
        removeImage(imageStoragePath + FILE_SEPARATOR + fileName);
    }
    
    private void removeImage(String filePath) {
        File f = new File(filePath);
        if(f.exists()) {
            f.delete();
            System.out.println("Old image deleted");
        }
    }
    
    
    /**
    * Move a image to specific path name and return image name. 
    * If new path name already exists return image name of that path name
    */
    private String moveImageAndReturnImageName(String currentImagePath, String newImagePath) {
        if(currentImagePath.isEmpty() || newImagePath.isEmpty()) {
            return "";
        }
        File from = new File(currentImagePath);
        if(!from.exists()) {
            return "";
        }
        File dest = new File(newImagePath);
        if(!from.renameTo(dest)) {
            return dest.getName();
        }
        return from.getName();
    }
    
    private String getFileType(MultipartFile multipartFile) {
        String contentType = multipartFile.getContentType();
        if(contentType != null) {
            return contentType.split("/")[1];
        }
        return "";
    }
    
    private String getNewFileName(String fileType) {
        return UUID.randomUUID().toString() + "." + fileType;
    }
    
    private String moveBookImageFromTempStorageToMainStorage(String imageFileName, String tempStoragePath, String storagePath) {
        if(imageFileName.isEmpty() || tempStoragePath.isEmpty() || storagePath.isEmpty()) {
            return "";
        }
        String tempImagePath = tempStoragePath + FILE_SEPARATOR + imageFileName;
        String realImagePath = storagePath + FILE_SEPARATOR + imageFileName;
        return moveImageAndReturnImageName(tempImagePath, realImagePath);
    }
    
}
