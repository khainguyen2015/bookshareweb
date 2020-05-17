/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.khai.bookshareweb.dto;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import javax.servlet.ServletContext;
import javax.servlet.http.HttpSessionBindingEvent;
import javax.servlet.http.HttpSessionBindingListener;

/**
 *
 * @author Acer
 */
public class TempBookImageListDTO implements HttpSessionBindingListener {
    
    private static final String FILE_SEPARATOR = System.getProperty("file.separator");
    
    private static final String TEMP_BOOK_IMAGES_STORAGE_PATH = "public" + FILE_SEPARATOR + "images" + FILE_SEPARATOR + "tempBookImages";
    
    private List<String> imageFileNameList = new ArrayList<>();
    
    private ServletContext servletContext;
    
    public TempBookImageListDTO(List<String> tempBookImageUrl, ServletContext servletContext) {
        this.imageFileNameList = tempBookImageUrl;
        this.servletContext = servletContext;
    }
    
    @Override
    public void valueUnbound(HttpSessionBindingEvent event) {
        imageFileNameList.stream()
                .filter((imageFileName) -> 
                        ((imageFileName != null) &&
                        (!imageFileName.isEmpty())
                    )
                )
            .forEach(bookImageUrl -> removeBookImage(bookImageUrl));
    }
    
    private void removeBookImage(String imageFileName) {
        String imageStoragePath = servletContext.getRealPath(TEMP_BOOK_IMAGES_STORAGE_PATH);
//        String fileName = getImageNameFromUrl(oldlUrl);
        File f = new File(imageStoragePath + FILE_SEPARATOR + imageFileName);
        if(f.exists()) {
            f.delete();
            System.out.println("Session valueUnbound Image deleted: " + imageFileName);
        }
    }
        
    private String getImageNameFromUrl(String bookImageUrl) {
        if(bookImageUrl != null && !bookImageUrl.isEmpty()) {
            return bookImageUrl.substring(bookImageUrl.lastIndexOf("/") + 1); 
        }
        return bookImageUrl;
    }

    public void addBookImageFileName(String imageFileName) {
        this.imageFileNameList.add(imageFileName);
    }
}
