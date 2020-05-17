/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.khai.bookshareweb.web;

import com.khai.bookshareweb.data.UserRepository;
import com.khai.bookshareweb.entity.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 *
 * @author Acer
 */

//@RestController
@RequestMapping("/api/v1/user/*")
public class UserRestController {
    
    @Autowired
    private UserRepository userRepository;
    
    
    @GetMapping(
        value = "/account/profile/{userId}",
        produces = { MediaType.APPLICATION_JSON_VALUE }
    )
    public User getUserById(@PathVariable("userId") int userId) {
        return userRepository.findOne(userId);
    }
    
}
