/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.khai.bookshareweb.data;

import com.khai.bookshareweb.entity.User;

/**
 *
 * @author Acer
 */
public interface UserRepository {

    public User findOne(int id);
    
    public User findByEmail(String email);
    
    public User findByUserName(String userName);
    
    public User save(User user);
    
    public User update(User user);
    
    public boolean remove(User user);
    
}
