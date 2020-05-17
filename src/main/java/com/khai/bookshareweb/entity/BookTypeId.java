/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.khai.bookshareweb.entity;

import java.io.Serializable;
import java.util.Objects;
import javax.persistence.Column;
import javax.persistence.Embeddable;

/**
 *
 * @author Acer
 */
@Embeddable
public class BookTypeId implements Serializable {
    
    @Column(name = "book_id")
    private int bookId;
    
    @Column(name = "type_id")
    private int typeId;

    public BookTypeId() {
    }
    
    public BookTypeId(int bookId, int typeId) {
        this.bookId = bookId;
        this.typeId = typeId;
    }
    
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
 
        if (o == null || getClass() != o.getClass())
            return false;
 
        BookTypeId that = (BookTypeId) o;
        return Objects.equals(bookId, that.bookId) &&
               Objects.equals(typeId, that.typeId);
    }
 
    @Override
    public int hashCode() {
        return 30;
    }

}
