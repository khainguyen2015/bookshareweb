/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.khai.bookshareweb.data;

import com.khai.bookshareweb.entity.BookType;
import com.khai.bookshareweb.entity.Type;
import com.khai.bookshareweb.entity.User;
import com.khai.bookshareweb.entity.UserAccount;
import javax.persistence.Query;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Fetch;
import javax.persistence.criteria.Root;
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
public class HibernateUserAccountRepository implements UserAccountRepository {
    
    private SessionFactory sessionFactory;
    
    public HibernateUserAccountRepository(SessionFactory sessionFactory) {
        this.sessionFactory = sessionFactory;
    }
    
    public Session currentSession() {
        return sessionFactory.getCurrentSession();
    }
    
    public UserAccount findByUsername(String username) {
        Query query =  currentSession()
            .createQuery(
                    "select ac " + 
                    "from UserAccount ac " +
                    "left join fetch ac.user " +
                    "where ac.accountName like :inputUsername", UserAccount.class)
            .setParameter("inputUsername", username);
        
        UserAccount userAccount = null;
        
        try {
            userAccount = (UserAccount)query.getSingleResult();
        } catch(Exception ex) {
        } 
        return userAccount;
        
        
//        CriteriaBuilder builder = currentSession().getCriteriaBuilder();
//        CriteriaQuery<UserAccount> criteria = builder.createQuery(UserAccount.class);
//        Root<UserAccount> root = criteria.from(UserAccount.class);
//        Fetch<UserAccount, User> bookFetch = root.fetch("user");
//        criteria.where( builder.equal( root.get("accountName"), username ) );
//        UserAccount result = currentSession().createQuery( criteria ).getSingleResult();
//        return result;
    } 
    
    
    public UserAccount save(UserAccount userAccount) {
        currentSession().persist(userAccount);
        return userAccount;
    }
    
    public UserAccount update(UserAccount userAccount) {
        userAccount = (UserAccount)currentSession().merge(userAccount);
        return userAccount;
    }
}
