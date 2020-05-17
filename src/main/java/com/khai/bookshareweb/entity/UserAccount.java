/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.khai.bookshareweb.entity;

import java.io.Serializable;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.OneToOne;
import javax.persistence.Table;

/**
 *
 * @author Acer
 */

@Entity()
@Table(name = "user_account")
public class UserAccount implements Serializable{
    
    @Id
    @Column(name = "account_name", nullable=false)
    private String accountName;
    
    @Column(name = "account_password", nullable=false)
    private String password;
    
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name="user_id", nullable=false)
    private User user;
    
    @Column(name = "role_id", nullable=false)
    private int roleId;
    
    @Column(name = "is_enable")
    private boolean enable;

    public boolean isEnable() {
        return enable;
    }

    public void setEnable(boolean enable) {
        this.enable = enable;
    }
    
    @OneToOne(mappedBy = "userAccount", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private VerificationToken verificationToken;

    public UserAccount() {
    }
    
    public UserAccount(String accountName, String password, User user, int roleId) {
        this.accountName = accountName;
        this.password = password;
        this.user = user;
        this.roleId = roleId;
    }


    public String getAccountName() {
        return accountName;
    }

    public String getPassword() {
        return password;
    }

    public User getUser() {
        return user;
    }

    public int getRoleId() {
        return roleId;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public void setRoleId(int roleId) {
        this.roleId = roleId;
    }

    public VerificationToken getVerificationToken() {
        return verificationToken;
    }

    public void setVerificationToken(VerificationToken verificationToken) {
        this.verificationToken = verificationToken;
    }
    
    
    
}
