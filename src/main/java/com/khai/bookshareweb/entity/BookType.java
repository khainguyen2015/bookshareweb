/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.khai.bookshareweb.entity;

import java.io.Serializable;
import java.util.Date;
import java.util.Objects;
import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.IdClass;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.MapsId;
import javax.persistence.Table;
import javax.persistence.Transient;

/**
 *
 * @author Acer
 */
@Entity
@Table(name = "book_and_it_type")
public class BookType implements Serializable{
    
    @EmbeddedId
    private BookTypeId id;
    
//    @Id
    @ManyToOne(fetch = FetchType.LAZY)
    @MapsId("bookId")
    @JoinColumn(name = "book_id", nullable = false)
    private Book book;
    
    @Transient
    private int createTime;
    
//    @Id
    @ManyToOne(fetch = FetchType.LAZY)
    @MapsId("typeId")
    @JoinColumn(name = "type_id", nullable = false)
    private Type type;
    
    public BookType() {
        createTime = (int)(Math.random() * 50 + 1);
        System.out.println("Create: " + createTime);
    }

    public BookType(Book book, Type type) {
        this.book = book;
        this.type = type;
        this.id = new BookTypeId(book.getBookId(), type.getTypeId());
    }

    public Book getBook() {
        return book;
    }

    public void setBook(Book book) {
        this.book = book;
    }

    public int getCreateTime() {
        return createTime;
    }

    public void setCreateTime(int createTime) {
        this.createTime = createTime;
    }

    public Type getType() {
        return type;
    }

    public void setType(Type type) {
        this.type = type;
    }
    
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
 
        if (o == null || getClass() != o.getClass())
            return false;
 
        BookType that = (BookType) o;
        return Objects.equals(book, that.book) &&
               Objects.equals(type, that.type);
    }
 
    @Override
    public int hashCode() {
        return 30;
    }
}
