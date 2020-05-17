/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.khai.bookshareweb.entity;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.Transient;

/**
 *
 * @author Acer
 */

@Entity()
@Table(name = "book")
public class Book implements Serializable{
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "book_id")
    private int bookId;
    
    @Column(name = "book_name")
    private String bookName;
    
    @Column(name = "author")
    private String author;
    
    @Column(name = "book_description")
    private String book_descripton;
    
    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "post_date")
    private Date postDate;
    
    @Column(name = "book_view")
    private int bookView;
    
    @Column(name = "status_code")
    private int statusCode;
    
    @Column(name = "thumbnail_url")
    private String thumbnailUrl;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name="user_id", nullable=false)
    private User user;
    
    @OneToMany(mappedBy = "book", cascade = CascadeType.ALL, orphanRemoval = true)
//    @Transient
    private List<BookType> types = new ArrayList<>();
    
    @OneToMany(mappedBy = "book", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<BookDownloadLink> bookDowloadLinks = new ArrayList<>();
    
    @OneToMany(mappedBy = "book", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<BookImage> bookImages = new ArrayList<>();

    public List<BookDownloadLink> getBookDowloadLinks() {
        return bookDowloadLinks;
    }
    
    public void addDownloadLink(BookDownloadLink link) {
        if(bookDowloadLinks.size() < 3) {
            link.setBook(this);
            bookDowloadLinks.add(link); 
        }
    }
    
    public void updateDownloadLink(int index, BookDownloadLink link) {
        bookDowloadLinks.get(index).setLink(link.getLink());
    }
    
    public void removeDownloadLink(int index) {
        bookDowloadLinks.get(index).setBook(null);
        bookDowloadLinks.remove(index);
    }

    public Book() {
        System.out.println("Create book");
    }

    public Book(int bookId, String bookName, String author, String book_descripton, Date postDate, int bookView, String thumbnailUrl, User user) {
        this.bookId = bookId;
        this.bookName = bookName;
        this.author = author;
        this.book_descripton = book_descripton;
        this.postDate = postDate;
        this.bookView = bookView;
        this.thumbnailUrl = thumbnailUrl;
        this.user = user;
    }

    public int getBookId() {
        return bookId;
    }

    public void setBookId(int bookId) {
        this.bookId = bookId;
    }

    public String getBookName() {
        return bookName;
    }

    public void setBookName(String bookName) {
        this.bookName = bookName;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public String getBook_descripton() {
        return book_descripton;
    }

    public void setBook_descripton(String book_descripton) {
        this.book_descripton = book_descripton;
    }

    public Date getPostDate() {
        return postDate;
    }

    public void setPostDate(Date postDate) {
        this.postDate = postDate;
    }

    public int getBookView() {
        return bookView;
    }

    public void setBookView(int bookView) {
        this.bookView = bookView;
    }

    public String getThumbnailUrl() {
        return thumbnailUrl;
    }

    public void setThumbnailUrl(String thumbnailUrl) {
        this.thumbnailUrl = thumbnailUrl;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public List<BookType> getTypes() {
        return types;
    }

    public void setTypes(List<Type> types) {
        this.types.clear();
        BookType bookType;
        for(Type type : types) {
            bookType = new BookType(this, type);
            this.types.add(bookType); 
        }
    }
    
    public void addType(Type type) {
        BookType bookType = new BookType( this, type );
        types.add(bookType);
        type.getBooks().add( bookType );
    }
    
    public List<Type> getType() {
        List<Type> bookTypes = new ArrayList<>();
        for(BookType bookType : types) {
            bookTypes.add(bookType.getType());
        }
        return bookTypes;
    }
    
    public void setType(Type type) {
        BookType bookType = new BookType(this, type);
        type.getBooks().add(bookType);
        if (this.types == null) {
            this.types = new ArrayList<>();
        } else {
            this.types.clear();
            this.types.add(bookType); 
        }
    }
    
    public void removeType(Type type) {
        BookType bookType = null;
        int index = 0;
        for(BookType bt : types) {
            if(bt.getBook().equals(this) && bt.getType().equals(type)) {
                bookType = bt;
                break;
            }
            index += 1;
        }
        
        if(bookType != null) {
            types.remove(bookType);
            type.getBooks().remove(bookType);
            bookType.setBook(null);
            bookType.setType(null);
        }
    }

    public List<BookImage> getBookImages() {
        return bookImages;
    }

    public void setBookImages(List<BookImage> bookImages) {
        for(BookImage image : bookImages) {
            image.setBook(this);
        }
        this.bookImages = bookImages;
    }
    
    public void addImage(BookImage image) {
        image.setBook(this);
        bookImages.add(image); 
    }
    
    public void updateImage(BookImage image) {
        for(BookImage bookImage : bookImages) {
            if(bookImage.getId() == image.getId()) {
                bookImage.setImageURL(image.getImageURL());
                return;
            }
        }
    }

    public int getStatusCode() {
        return statusCode;
    }

    public void setStatusCode(int statusCode) {
        this.statusCode = statusCode;
    }
    
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Book book = (Book) o;
        return bookId > 0 && bookId == book.bookId;
    }
 
    @Override
    public int hashCode() {
        return 30;
    }

}
