/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.khai.bookshareweb.event.listener;

import com.khai.bookshareweb.entity.BookImage;
import java.io.File;
import java.util.List;
import javax.servlet.ServletContext;
import javax.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationListener;
import org.springframework.security.core.session.SessionDestroyedEvent;
import org.springframework.stereotype.Component;

/**
 *
 * @author Acer
 */
@Component
public class SessionEndedListener implements ApplicationListener<SessionDestroyedEvent> {
    
    private static final String FILE_SEPARATOR = System.getProperty("file.separator");
    
    private static final String TEMP_IMAGE_LIST_OBJECT_NAME = "updateTempImage";
    
    private static final String TEMP_BOOK_IMAGES_STORAGE_PATH = "public" + FILE_SEPARATOR + "images" + FILE_SEPARATOR + "temp_thumbnails";
    
    @Autowired
    private HttpSession httpSession;
    
    @Autowired
    private ServletContext servletContext;

    @Override
    public void onApplicationEvent(SessionDestroyedEvent event) {
        System.out.println("Session ended");
        System.out.println("Session ended");
        System.out.println("Session ended");
//        clearTempImageWhenSessionEnded(httpSession);
    }
    
    private void clearTempImageWhenSessionEnded(HttpSession httpSession) {
        String attributeName;
        while(httpSession.getAttributeNames().hasMoreElements()) {
            attributeName = httpSession.getAttributeNames().nextElement();
            System.out.println(attributeName);
            if(attributeName.contains(TEMP_IMAGE_LIST_OBJECT_NAME)) {
                List<BookImage> tempBookImageList = (List)httpSession.getAttribute(attributeName);
                removeImageList(tempBookImageList);
            }
        }
    }
    
    private void removeImageList(List<BookImage> tempBookImageList) {
        tempBookImageList.stream()
            .filter((tempBookImage) -> (tempBookImage != null))
            .filter((tempBookImage) -> (!tempBookImage.getImageURL().isEmpty()))
            .forEachOrdered((tempBookImage) -> {removeBookImage(tempBookImage.getImageURL());});
    }
    
  
    private void removeBookImage(String oldlUrl) {
        String imageStoragePath = servletContext.getRealPath(TEMP_BOOK_IMAGES_STORAGE_PATH);
        String fileName = getImageNameFromUrl(oldlUrl);
        File f = new File(imageStoragePath + FILE_SEPARATOR + fileName);
        if(f.exists()) {
            f.delete();
            System.out.println("Image deleted: " + fileName);
        }
    }
        
    private String getImageNameFromUrl(String bookImageUrl) {
        if(bookImageUrl != null && !bookImageUrl.isEmpty()) {
            return bookImageUrl.substring(bookImageUrl.lastIndexOf("/") + 1); 
        }
        return bookImageUrl;
    }
    
}
