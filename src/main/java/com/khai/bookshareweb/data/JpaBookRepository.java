/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.khai.bookshareweb.data;

import com.khai.bookshareweb.entity.Book;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 *
 * @author Acer
 */
public interface JpaBookRepository extends JpaRepository<Book, Integer>, BookRepository {
    
}
