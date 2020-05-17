/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.khai.bookshareweb.entity;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.OrderBy;
import javax.persistence.OrderColumn;
import javax.persistence.Table;

/**
 *
 * @author Acer
 */
@Entity()
@Table(name = "book_type")
public class Type implements Serializable{
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "type_id")
    private int typeId;
    
    @Column(name = "type_name")
    private String typeName;
    
    @OneToMany(mappedBy = "type", cascade = CascadeType.ALL, orphanRemoval = true)
//    @OrderColumn(name = "book_id")
    private List<BookType> books = new ArrayList<>();
    
//    @OneToMany(mappedBy = "type", cascade = CascadeType.ALL, orphanRemoval = true)
//    private Set<BookType> books = new HashSet<>();
    
    
    public Type() {
        
    }

    public Type(int typeId, String typeName) {
        this.typeId = typeId;
        this.typeName = typeName;
    }

    public int getTypeId() {
        return typeId;
    }

    public void setTypeId(int typeId) {
        this.typeId = typeId;
    }

    public String getTypeName() {
        return typeName;
    }

    public void setTypeName(String typeName) {
        this.typeName = typeName;
    }

    public List<BookType> getBooks() {
        return books;
    }
    
//    public Set<BookType> getBooks() {
//        return books;
//    }
    

    public List<Book> getBooksSameType() {
        List<Book> booksSameType = new ArrayList<>();
        for(BookType bookType : books) {
            booksSameType.add(bookType.getBook());
        }
        return booksSameType;
    }
    
    public void addBook(Book book) {
        BookType bookType = new BookType(book, this);
        books.add(bookType);
//        book.getTypes().add(bookType);
    }
    
    
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        
        if (o == null || getClass() != o.getClass()) return false;
        
        Type type = (Type) o;
        return typeId > 0 && typeId == type.typeId;
    }
 
    @Override
    public int hashCode() {
        return 30;
    }
}
