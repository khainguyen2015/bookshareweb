/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.khai.bookshareweb.data;

import com.khai.bookshareweb.entity.BookImage;
import java.util.List;
import javax.persistence.Query;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

/**
 *
 * @author Acer
 */
@Repository
@Transactional
public class HibernateBookImageRepository implements BookImageRepository {
    
    private SessionFactory sessionFactory;
    
    public HibernateBookImageRepository(SessionFactory sessionFactory) {
        this.sessionFactory = sessionFactory;
    }
    
    private Session currentSession() {
        return sessionFactory.getCurrentSession();
    }
    
    
    public List<BookImage> getAll() {
        Query query = currentSession().createQuery(
                "select b " +
                "from BookImage b", BookImage.class
        );
        return query.getResultList();
    }
    
}
