/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.khai.bookshareweb.validation;

import com.khai.bookshareweb.entity.BookDownloadLink;
import java.util.List;
import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

/**
 *
 * @author Acer
 */
public class BookDownloadLinkValidator implements ConstraintValidator<ValidBookDownloadLinks, Object> {
    
    private static final String URL_REGEX = "<\\b(https?|ftp|file)://[-a-zA-Z0-9+&@#/%?=~_|!:,.;]*[-a-zA-Z0-9+&@#/%=~_|]>";
    
    @Override
    public void initialize(ValidBookDownloadLinks validBookDownloadLinks) {
        
    }
    
    @Override
    public boolean isValid(Object obj, ConstraintValidatorContext context) {
        List<BookDownloadLink> bookDownloadLinks = (List<BookDownloadLink>)obj;
        for(BookDownloadLink bookDownloadLink : bookDownloadLinks) {
            if(!bookDownloadLink.getLink().isEmpty()) {
                if(bookDownloadLink.getLink().matches(URL_REGEX)) {
                    return true;
                }
            }
        }
        return false;
    }
    
}
