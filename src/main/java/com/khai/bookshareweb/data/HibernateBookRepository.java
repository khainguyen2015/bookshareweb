/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.khai.bookshareweb.data;

import com.khai.bookshareweb.dto.BookDTO;
import com.khai.bookshareweb.entity.Book;
import com.khai.bookshareweb.entity.BookDownloadLink;
import com.khai.bookshareweb.entity.BookType;
import com.khai.bookshareweb.entity.User;
import com.khai.bookshareweb.entity.Type;
import com.khai.bookshareweb.entity.UserAccount;
import java.io.Serializable;
import java.util.List;
import java.util.Properties;
import javax.persistence.NoResultException;
import javax.persistence.Query;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.Root;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Fetch;
import javax.persistence.criteria.Join;

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
public class HibernateBookRepository implements BookRepository{
    
    public static final int BOOK_APPROVED_STATUS_CODE = 1;
    public static final int BOOK_NOT_APPROVED_STATUS_CODE = 0;
    public static final int BOOK_BLOCKED_STATUS_CODE = 2;
    
    private SessionFactory sessionFactory;
    
    public HibernateBookRepository(SessionFactory sessionFactory) {
        this.sessionFactory = sessionFactory;
    }
    
    private Session currentSession() {
        return sessionFactory.getCurrentSession();
    }
    
    public long count() {
        return findAll().size();
    }
    
    @Override
    public Book add(Book book) {
        currentSession().save(book);
        return book;
    }
    
    @Override
    public Book save(Book book) {
        Book bookAfterMerge = (Book)currentSession().merge(book);
        return bookAfterMerge;
    }
    
    @Override
    public boolean isBookExist(int bookId) {
        Book book = currentSession().get(Book.class, bookId);
        return book != null;
    }
    
    
    @Override
    public Book findAnApprovedBook(int id) {
//        CriteriaBuilder builder = currentSession().getCriteriaBuilder();
//        CriteriaQuery<Book> criteria = builder.createQuery(Book.class);
//        
//        Root<Book> root = criteria.from(Book.class);
//        Fetch<Book, BookType> bookTypeFetch = root.fetch("types");
//        
//        criteria.where( builder.equal( root.get("bookId"), id ) );
//        Book result = currentSession().createQuery( criteria ).getSingleResult();
////        result.getBookDowloadLinks().size();
////        System.out.println(result.getTypes().size());
//        System.out.println("abcd");
////        result.getTypes().get(0).getType().getBooks().size();
////        Book result = currentSession().find(Book.class, id);
        Query query = currentSession()
        .createQuery(
                "select b " + 
                "from Book b " +
                "join fetch b.types bt " +
                "join fetch bt.type " +
                "where b.bookId = :bookId " +
                "and b.statusCode = :statusCode", Book.class)
        .setParameter("bookId", id)
        .setParameter("statusCode", BOOK_APPROVED_STATUS_CODE);
        Book book = (Book)query.getSingleResult();
        
        book = currentSession()
        .createQuery(
                "select b " + 
                "from Book b " +
                "left join fetch b.bookDowloadLinks " +
                "where b.bookId = :bookId", Book.class)
        .setParameter("bookId", book.getBookId()).getSingleResult();
        
        book = currentSession()
        .createQuery(
                "select b " +
                "from Book b " +
                "left join fetch b.bookImages " +
                "where b.bookId = :bookId", Book.class)
        .setParameter("bookId", book.getBookId()).getSingleResult();
        
        return book;
    }
    
    @Override
    public Book findOne(int id) {
        Book book;
        Query query = currentSession()
        .createQuery(
                "select b " + 
                "from Book b " +
                "join fetch b.types bt " +
                "join fetch bt.type " +
                "where b.bookId = :bookId", Book.class)
        .setParameter("bookId", id);
        try {
            book = (Book)query.getSingleResult();
        } catch(NoResultException ex) {
            return null;
        }
        
        book = currentSession()
        .createQuery(
                "select b " + 
                "from Book b " +
                "left join fetch b.bookDowloadLinks " +
                "where b.bookId = :bookId", Book.class)
        .setParameter("bookId", book.getBookId()).getSingleResult();
        
        book = currentSession()
        .createQuery(
                "select b " +
                "from Book b " +
                "left join fetch b.bookImages " +
                "where b.bookId = :bookId", Book.class)
        .setParameter("bookId", book.getBookId()).getSingleResult();
        return book;
    }
    
    @Override
    public Book findByBookName(String bookName) {
        CriteriaBuilder builder = currentSession().getCriteriaBuilder();
        CriteriaQuery<Book> criteria = builder.createQuery(Book.class);
        Root<Book> root = criteria.from(Book.class);
        criteria.select(root);
        criteria.where(builder.equal(root.get("bookName"), bookName));
        List<Book> results = currentSession().createQuery(criteria).getResultList();
        return results.get(0);
    }
    
    @Override
    public List<Book> findAll() {
        CriteriaBuilder builder = currentSession().getCriteriaBuilder();
        CriteriaQuery<Book> criteria = builder.createQuery(Book.class);
        Root<Book> root = criteria.from(Book.class);
        Join<Book, User> userJoin = root.join("user");
        criteria.select(root);
        return currentSession().createQuery(criteria).getResultList();
    }
    
    @Override
    public List<BookDTO> findAll_LiteVersion() {
        Query query = currentSession()
        .createQuery(
                "select new " +
                "com.khai.bookshareweb.dto.BookDTO(" +
                    "b.bookId, b.bookName, b.author, b.bookView, b.thumbnailUrl, u.userId, b.postDate" +
                ") " +
                "from Book b " +
                "join b.user u", BookDTO.class);
        return query.getResultList();
    }
    
    @Override
    public List<Book> findByUserId(int userId) {
        Query query =  currentSession()
        .createQuery(
                "select b " + 
                "from Book b " +
                "left join fetch b.user " +
                "where b.user.userId = :inputUserId", Book.class)
        .setParameter("inputUserId", userId);
        
        return query.getResultList();
    }
    
    
    @Override
    public void remove(Book book) {
        System.out.println(book.getBookName());
        System.out.println("Book contains: " + currentSession().contains(book));
        currentSession().remove(book);
    }
    
    
    @Override
    public List<BookDTO> searchBookWithLimit(int max, String searchKey) {
        Query query = currentSession()
            .createQuery(
                "select " +
                "new com.khai.bookshareweb.dto.BookDTO(" +
                    "b.bookId, b.bookName, b.author, b.bookView, b.thumbnailUrl, u.userId" +
                ") " +
                "from Book b " +
                "join b.user u " +
                "where b.bookName like :searchKey " +
                "and b.statusCode = :statusCode " +
                "order by b.bookView DESC ", BookDTO.class)
            .setParameter("searchKey", "%" + searchKey + "%")
            .setParameter("statusCode", BOOK_APPROVED_STATUS_CODE)
            .setMaxResults(max);
        return query.getResultList();
    }
    
    @Override
    public List<BookDTO> searchBookWithLimit(int max, String searchKey, int userId) {
        Query query = currentSession()
            .createQuery(
                "select " +
                "new com.khai.bookshareweb.dto.BookDTO(" +
                    "b.bookId, b.bookName, b.author, b.bookView, b.thumbnailUrl, u.userId, b.statusCode" +
                ") " +
                "from Book b " +
                "join b.user u " +
                "where u.userId = :userId " +
                "and b.bookName like :searchKey " +
                "order by b.bookView DESC ", BookDTO.class)
            .setParameter("searchKey", "%" + searchKey + "%")
            .setMaxResults(max);
        return query.getResultList();
    }
    
    
    @Override
    public List<BookDTO> findAll_LiteVersion(int maxResult, int firstResultIndex) {
        Query query = currentSession()
            .createQuery(
                "select " +
                "new com.khai.bookshareweb.dto.BookDTO(" +
                    "b.bookId, b.bookName, b.author, b.bookView, b.thumbnailUrl, u.userId, b.postDate" +
                ") " +
                "from Book b " +
                "join b.user u " +
                "where b.statusCode = :statusCode " +
                "order by b.bookView DESC ", BookDTO.class)
            .setFirstResult(firstResultIndex)
            .setParameter("statusCode", BOOK_APPROVED_STATUS_CODE)
            .setMaxResults(maxResult);
        return query.getResultList();
    }
    
    @Override
    public List<BookDTO> findAll_LiteVersion(int maxResult, int firstResultIndex, int userId) {
        if(userId < 0) {
            return findAll_LiteVersion(maxResult, firstResultIndex);
        }
        
        Query query = currentSession()
            .createQuery(
                "select new " +
                    "com.khai.bookshareweb.dto.BookDTO(" +
                        "b.bookId, b.bookName, b.author, b.bookView, b.thumbnailUrl, u.userId, b.postDate, b.statusCode" +
                    ") " +
                "from Book b " +
                "join b.user u " +
                "where u.userId = :userId " +
                "order by b.bookView DESC", BookDTO.class)
            .setParameter("userId", userId)
            .setFirstResult(firstResultIndex)
            .setMaxResults(maxResult);
        return query.getResultList();
    }
    
    public List<BookDTO> findAll_LiteVersion(Properties propertiesForFind, BooksSelectedOrderBy orderBy, int userId) {
        int maxResult = Integer.parseInt(propertiesForFind.getProperty("maxResults"));
        int firstResultIndex = Integer.parseInt(propertiesForFind.getProperty("firstResultIndex"));
        String oderBy = "b.bookView";
        
        switch(orderBy) {
            case BOOK_VIEW:
                break;
            case BOOK_POSTED_DATE:
                oderBy = "b.postDate";
                break;
        }

        Query query = currentSession()
            .createQuery(
                "select new " +
                    "com.khai.bookshareweb.dto.BookDTO(" +
                        "b.bookId, b.bookName, b.author, b.bookView, b.thumbnailUrl, u.userId, b.postDate, b.statusCode" +
                    ") " +
                "from Book b " +
                "join b.user u " +
                "where u.userId = :userId " + 
                "order by "+ oderBy + " DESC", BookDTO.class)
            .setParameter("userId", userId)
            .setFirstResult(firstResultIndex)
            .setMaxResults(maxResult);
        return query.getResultList();
    }
    
    @Override
    public long getAmountOfBooks() {
        Query query = currentSession()
            .createQuery(
                "select count(b.bookId) " +
                "from Book b " +
                "where b.statusCode = :statusCode", Long.class)
            .setParameter("statusCode", BOOK_APPROVED_STATUS_CODE);
        
        return (Long)query.getSingleResult();
    }
    
    @Override
    public long getAmountOfBooks(int userId) {
        if(userId < 0) {
            return getAmountOfBooks();
        }
        
        Query query = currentSession()
            .createQuery(
                "select count(b.bookId) " +
                "from Book b " +
                "join b.user u " +
                "where u.userId = :userId", Long.class)
            .setParameter("userId", userId);
        
        return (Long)query.getSingleResult();
    }
    
    @Override
    public List<BookDTO> searchBookWithSearchPropertiesAndOrderBy(Properties searchProperties, BooksSelectedOrderBy orderBy) {
        int maxResult = Integer.parseInt(searchProperties.getProperty("maxResult"));
        int firstResultIndex = Integer.parseInt(searchProperties.getProperty("firstResultIndex"));
        String searchKey = searchProperties.getProperty("searchKey");
        String oderBy = "b.bookView";
        switch(orderBy) {
            case BOOK_VIEW:
                break;
            case BOOK_POSTED_DATE:
                oderBy = "b.postDate";
                break;
        }
        Query query = currentSession()
            .createQuery(
                "select " +
                "new com.khai.bookshareweb.dto.BookDTO(" +
                    "b.bookId, b.bookName, b.author, b.bookView, b.thumbnailUrl, u.userId, b.postDate" +
                ") " +
                "from Book b " + 
                "join b.user u " +
                "where b.bookName like :searchKey " +
                "and b.statusCode = :statusCode " +
                "order by "+ oderBy + " DESC", BookDTO.class)
            .setParameter("searchKey", "%" + searchKey + "%")
            .setParameter("statusCode", BOOK_APPROVED_STATUS_CODE)
            .setFirstResult(firstResultIndex) 
            .setMaxResults(maxResult);
        return query.getResultList();
    }
    
    @Override
    public List<BookDTO> searchBookWithSearchPropertiesAndOrderBy(Properties searchProperties, BooksSelectedOrderBy orderBy, int userId) {
        if(userId < 0) {
            return searchBookWithSearchPropertiesAndOrderBy(searchProperties, orderBy);
        }
        
        int maxResult = Integer.parseInt(searchProperties.getProperty("maxResult"));
        int firstResultIndex = Integer.parseInt(searchProperties.getProperty("firstResultIndex"));
        String searchKey = searchProperties.getProperty("searchKey");
        String oderBy = "b.bookView";
        switch(orderBy) {
            case BOOK_VIEW:
                break;
            case BOOK_POSTED_DATE:
                oderBy = "b.postDate";
                break;
        }
        Query query = currentSession()
            .createQuery(
                "select " +
                "new com.khai.bookshareweb.dto.BookDTO(" +
                    "b.bookId, b.bookName, b.author, b.bookView, b.thumbnailUrl, u.userId, b.postDate" +
                ") " +
                "from Book b " + 
                "join b.user u " +
                "where u.userId = :userId " +
                "and b.bookName like :searchKey " +
                "order by "+ oderBy + " DESC", BookDTO.class)
            .setParameter("userId", userId)
            .setParameter("searchKey", "%" + searchKey + "%")
            .setFirstResult(firstResultIndex)
            .setMaxResults(maxResult);
        return query.getResultList();
    }
    
    
    @Override
    public long getAmountOfBooks(String searchKey) {
        Query query = currentSession()
            .createQuery(
                "select count(b.bookId) " +
                "from Book b " +
                "where b.bookName like :searchKey " +
                "and b.statusCode = :statusCode" , Long.class)
            .setParameter("searchKey", "%" + searchKey + "%")
            .setParameter("statusCode", BOOK_APPROVED_STATUS_CODE);
        
        return (Long)query.getSingleResult();
        
    }
    
    @Override
    public long getAmountOfBooks(String searchKey, int userId) {
        if(userId < 0) {
            return getAmountOfBooks(searchKey);
        }
        
        Query query = currentSession()
            .createQuery(
                "select count(b.bookId) " +
                "from Book b " +
                "join b.user u " +
                "where u.userId = :userId " +
                "and b.bookName like :searchKey ", Long.class)
            .setParameter("userId", userId)
            .setParameter("searchKey", "%" + searchKey + "%");
        
        return (Long)query.getSingleResult();
    }
    
    @Override
    public List<BookDTO> findAllBooksNotAprrove(int maxResult, int firstResultIndex) {
        Query query = currentSession()
            .createQuery(
                "select new " +
                    "com.khai.bookshareweb.dto.BookDTO(" +
                        "b.bookId, b.bookName, b.author, b.bookView, b.thumbnailUrl, u.userId, b.postDate, b.statusCode, u.userName" +
                    ") " +
                "from Book b " +
                "join b.user u " +
                "where b.statusCode = :statusCode " +
                "order by b.bookView DESC", BookDTO.class)
            .setParameter("statusCode", BOOK_NOT_APPROVED_STATUS_CODE)
            .setFirstResult(firstResultIndex)
            .setMaxResults(maxResult);
        return query.getResultList();
    }
    
}
