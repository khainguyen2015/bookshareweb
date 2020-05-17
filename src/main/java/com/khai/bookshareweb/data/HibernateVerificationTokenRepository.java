/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.khai.bookshareweb.data;

import com.khai.bookshareweb.entity.VerificationToken;
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
public class HibernateVerificationTokenRepository implements VerificationTokenRepository {
    
    
    private SessionFactory sessionFactory;
    
    @Autowired
    public HibernateVerificationTokenRepository(SessionFactory sessionFactory) {
        this.sessionFactory = sessionFactory;
    }
    
    private Session currentSession() {
        return sessionFactory.getCurrentSession();
    }
    

    @Override
    public VerificationToken save(VerificationToken verificationToken) {
        currentSession().persist(verificationToken);
        return verificationToken;
    }
    
    @Override
    public VerificationToken findByToken(String token) {
        Query query = currentSession()
                .createQuery(
                    "select vt " +
                    "from VerificationToken vt " +
                    "where vt.token like :inputToken", VerificationToken.class)
                .setParameter("inputToken", token);
        
        VerificationToken verificationToken = null;
        try {
            verificationToken = (VerificationToken)query.getSingleResult();
        } catch(Exception ex) {
            ex.printStackTrace();
        }
        return verificationToken;
    }
    
}
