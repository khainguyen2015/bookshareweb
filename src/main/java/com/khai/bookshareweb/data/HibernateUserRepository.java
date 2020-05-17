/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.khai.bookshareweb.data;

import com.khai.bookshareweb.entity.Book;
import com.khai.bookshareweb.entity.BookType;
import com.khai.bookshareweb.entity.Type;
import com.khai.bookshareweb.entity.User;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Fetch;
import javax.persistence.criteria.Root;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.query.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

/**
 *
 * @author Acer
 */
@Repository
@Transactional
public class HibernateUserRepository implements UserRepository {
    
    private SessionFactory sessionFactory;
    
    public HibernateUserRepository(SessionFactory sessionFactory) {
        this.sessionFactory = sessionFactory;
    }
    
    private Session currentSession() {
        return sessionFactory.getCurrentSession();
    }
    
    public User findOne(int id) {
        CriteriaBuilder builder = currentSession().getCriteriaBuilder();
        CriteriaQuery<User> criteria = builder.createQuery(User.class);
        Root<User> root = criteria.from(User.class);
        Fetch<User, Book> bookFetch = root.fetch("books");
        criteria.where( builder.equal( root.get("userId"), id ) );
        User result = currentSession().createQuery( criteria ).getSingleResult();
//        result.getTypes().get(0).getType().getBooks().size();
//        Book result = currentSession().find(Book.class, id);
        return result;
    }
    
    
    
    public User findByEmail(String email) {
        Query query =  currentSession()
            .createQuery(
                    "select ui " + 
                    "from user_info ui " +
                    "where ui.email like :inputEmail", User.class)
            .setParameter("inputEmail", email);
        
        User user = null;
        
        try {
            user = (User)query.getSingleResult();
        } catch(Exception ex) {
        } 
        return user;
    }
    
    public User findByUserName(String userName) {
        Query query =  currentSession()
            .createQuery(
                    "select ui " + 
                    "from user_info ui " +
                    "where ui.userName like :inputUserName", User.class)
            .setParameter("inputUserName", userName);
        
        User user = null;
        
        try {
            user = (User)query.getSingleResult();
        } catch(Exception ex) {
            ex.printStackTrace();
        } 
        return user;
    }
    
    public User save(User user) {
        currentSession().persist(user);
        return user;
    }
    
    public User update(User user) {
        User newUser = (User)currentSession().merge(user);
        System.out.println(user.getUserName());
        System.out.println(newUser.getUserName());
        System.out.println(newUser == user);
        return newUser;
    }
    
    public boolean remove(User user) {
        try {
            currentSession().remove(user);
        } catch(Exception ex) {
            return false;
        }
        return true;
    }
    
}
