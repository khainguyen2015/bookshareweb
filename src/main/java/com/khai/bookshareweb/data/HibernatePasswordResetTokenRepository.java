/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.khai.bookshareweb.data;

import com.khai.bookshareweb.entity.PasswordResetToken;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.query.Query;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

/**
 *
 * @author Acer
 */
@Repository
@Transactional
public class HibernatePasswordResetTokenRepository implements PasswordResetTokenRepository {
    
    private SessionFactory sessionFactory;
    
    @Autowired
    public HibernatePasswordResetTokenRepository(SessionFactory sessionFactory) {
        this.sessionFactory = sessionFactory;
    }
    
    private Session currentSession() {
        return sessionFactory.getCurrentSession();
    }
    
    @Override
    public PasswordResetToken save(PasswordResetToken passwordResetToken) {
        currentSession().persist(passwordResetToken);
        return passwordResetToken;
    }
    
    
    @Override
    public PasswordResetToken remove(PasswordResetToken passwordResetToken) {
        return null;
    }
    
    @Override
    public PasswordResetToken findByToken(String token) {
        Query query = currentSession()
                .createQuery(
                        "select prt "
                        + "from PasswordResetToken prt "
                        + "where prt.token like :inputToken", PasswordResetToken.class)
                .setParameter("inputToken", token);
        PasswordResetToken passwordResetToken = null;
        try {
            passwordResetToken = (PasswordResetToken)query.getSingleResult();
        } catch(Exception ex) {
            ex.printStackTrace();
        }
        return passwordResetToken;
    }
        
}
