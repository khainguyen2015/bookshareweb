/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.khai.bookshareweb.entity;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import org.springframework.transaction.annotation.Transactional;

/**
 *
 * @author Acer
 */
@Entity(name = "user_info")
public class User implements Serializable {
    
    @Id
    @GeneratedValue(strategy=GenerationType.IDENTITY)
    @Column(name = "user_id", nullable=false)
    private int userId;
    
    @Column(name = "user_name")
    private String userName;
    
    @Column(name = "email")
    private String email;
    
    @Column(name = "avatar_url")
    private String avatarUrl;
   
    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Book> books = new ArrayList<>();
    
    @OneToOne(
        mappedBy = "user",
        cascade = CascadeType.ALL,
        orphanRemoval = true,
        fetch = FetchType.LAZY
    )
    private UserAccount userAccount;

    public UserAccount getUserAccount() {
        return userAccount;
    }

    public void setUserAccount(UserAccount userAccount) {
        this.userAccount = userAccount;
    }
    
    public User() {
        
    }

    public User(int userId, String userName, String email, String avatarUrl) {
        this.userId = userId;
        this.userName = userName;
        this.email = email;
        this.avatarUrl = avatarUrl;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getAvatarUrl() {
        return avatarUrl;
    }

    public void setAvatarUrl(String avatarUrl) {
        this.avatarUrl = avatarUrl;
    }
    
    public List<Book> getBooks() {
        return books;
    }
    
   
    
    public void addBook(Book book) {
        books.add( book );
        book.setUser( this );
    }

    public void removeBook(Book book) {
        books.remove( book );
        book.setUser( null );
    }
   
    
}
