/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.khai.bookshareweb.data;

import com.khai.bookshareweb.entity.Book;
import com.khai.bookshareweb.entity.BookType;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import com.khai.bookshareweb.entity.Type;
import com.khai.bookshareweb.entity.User;
import java.util.ArrayList;
import java.util.List;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Fetch;
import javax.persistence.criteria.Root;
import org.hibernate.query.Query;

/**
 *
 * @author Acer
 */

@Repository
@Transactional
public class HibernateTypeRepository implements TypeRepository {
    
    private SessionFactory sessionFactory;
    
    public HibernateTypeRepository(SessionFactory sessionFactory) {
        this.sessionFactory = sessionFactory;
    }
    
    private Session currentSession() {
        return sessionFactory.getCurrentSession();
    }
    
    @Override
    public Type update(Type type) {
        return (Type)currentSession().merge(type);
    }
    
    
    public Type findOne(int id) {
        CriteriaBuilder builder = currentSession().getCriteriaBuilder();
        CriteriaQuery<Type> criteria = builder.createQuery(Type.class);
        Root<Type> root = criteria.from(Type.class);
//        Fetch<Type, BookType> bookFetch = root.fetch("books");
        criteria.where( builder.equal( root.get("typeId"), id ) );
        Type result = currentSession().createQuery( criteria ).getSingleResult();
        return result;
    }
    
    public List<Type> findAll() {
        Query query = currentSession().createQuery("select t from Type t", Type.class);
        List<Type> types = new ArrayList<>();
        types = query.getResultList();
        return types;
    }
    
    public List<Type> findTypesByIds(List<Integer> typeIds) {
        Query query = currentSession().createQuery(
            "select distinct t "
            + "from Type t "
            + "left join fetch t.books b "
            + "where t.typeId IN :typeIds", 
            Type.class);
        query.setParameter("typeIds", typeIds)
            .setHint("hibernate.query.passDistinctThrough", false);
        return query.getResultList();
    }
    
}
