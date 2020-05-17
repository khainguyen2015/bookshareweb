/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.khai.bookshareweb.service;

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import org.springframework.stereotype.Service;
/**
 *
 * @author Acer
 */
@Service
public class LoginAttemptService {
    
    private static final int MAX_ATTEMPT = 5;
    
    private LoadingCache<String, Integer> cache = null;
    
    public LoginAttemptService() {
        if(cache != null) {
            return;
        }
        CacheLoader<String, Integer> cacheLoader;
        cacheLoader = new CacheLoader<String, Integer>() {
            @Override
            public Integer load(String key) {
                return 0;
            } 
        };
        cache = CacheBuilder.newBuilder().expireAfterWrite(5, TimeUnit.MINUTES).build(cacheLoader);        
    }
    
    public boolean loginSuccess(String key) {
        System.out.println("Clear login attempt cache");
        cache.invalidate(key);
        return true;
    }
    
    public boolean isBlocked(String key) {
        try {
            return cache.get(key) > MAX_ATTEMPT;
        } catch(ExecutionException ex) {
            ex.printStackTrace();
            return false;
        }
    }
    
    public int loginFailed(String key) {
        int numberOfTries = 0;
        try {
            numberOfTries = cache.get(key);
        } catch(ExecutionException ex) {
            ex.printStackTrace();
        }
        numberOfTries += 1;
        cache.put(key, numberOfTries);
        return numberOfTries;
    }
    
}
