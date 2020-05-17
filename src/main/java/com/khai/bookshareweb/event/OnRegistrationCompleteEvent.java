/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.khai.bookshareweb.event;

import com.khai.bookshareweb.entity.UserAccount;
import java.util.Locale;
import org.springframework.context.ApplicationEvent;

/**
 *
 * @author Acer
 */
public class OnRegistrationCompleteEvent extends ApplicationEvent {
    
    private String appUrl;
    private UserAccount userAccount;
    private Locale locale;

    public OnRegistrationCompleteEvent(String appUrl, UserAccount userAccount, Locale locale) {
        super(userAccount);
        this.appUrl = appUrl;
        this.userAccount = userAccount;
        this.locale = locale;
    }

    public String getAppUrl() {
        return appUrl;
    }

    public void setAppUrl(String appUrl) {
        this.appUrl = appUrl;
    }

    public UserAccount getUserAccount() {
        return userAccount;
    }

    public void setUserAccount(UserAccount userAccount) {
        this.userAccount = userAccount;
    }

    public Locale getLocale() {
        return locale;
    }

    public void setLocale(Locale locale) {
        this.locale = locale;
    }
    
    
}
