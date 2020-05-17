/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.khai.bookshareweb.data;

import com.khai.bookshareweb.entity.Type;
import java.util.List;

/**
 *
 * @author Acer
 */
public interface TypeRepository {
    
    public Type findOne(int id);
    
    public List<Type> findAll();
    
    public List<Type> findTypesByIds(List<Integer> typeIds);
    
    public Type update(Type type);
    
}
