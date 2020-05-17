/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.khai.bookshareweb.service;

import com.google.common.io.Files;
import com.khai.bookshareweb.common.DateUtils;
import com.khai.bookshareweb.data.BookRepository;
import com.khai.bookshareweb.data.HibernateBookRepository;
import com.khai.bookshareweb.data.TypeRepository;
import com.khai.bookshareweb.dto.BookCreationDTO;
import com.khai.bookshareweb.dto.BookDTO;
import com.khai.bookshareweb.dto.BookImageDTO;
import com.khai.bookshareweb.dto.BookUpdateDTO;
import com.khai.bookshareweb.entity.Book;
import com.khai.bookshareweb.entity.BookDownloadLink;
import com.khai.bookshareweb.entity.BookImage;
import com.khai.bookshareweb.entity.BookType;
import com.khai.bookshareweb.entity.Type;
import com.khai.bookshareweb.entity.User;
import com.khai.bookshareweb.service.exception.BookNotFoundException;
import com.khai.bookshareweb.service.exception.TypeNotFoundException;
import com.khai.bookshareweb.service.exception.UserIdNotMatchesException;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.ListIterator;
import java.util.Objects;
import java.util.Properties;
import java.util.UUID;
import java.util.function.Predicate;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Stream;
import javax.servlet.ServletContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.util.HtmlUtils;

/**
 *
 * @author Acer
 */
@Service
public class BookServiceImpl implements BookService {
    private static final Integer NUMBERS_OF_BOOK_DOWNLOAD_LINKS_USER_CAN_CREATE = 3;
    private static final Integer NUMBERS_OF_BOOK_IMAGES_USER_CAN_UPLOAD = 3;
    private static final String BOOK_DOWNLOAD_URL_REGEX = "\\b(https?|ftp|file)://[-a-zA-Z0-9+&@#/%?=~_|!:,.;]*[-a-zA-Z0-9+&@#/%=~_|]";
    private static final String BOOK_COVER_IMAGE_URL_PREFIX = "images/thumbnails/";
    private static final String BOOK_IMAGE_URL_PREFIX = "images/bookImages/";
    private static final String TEMP_BOOK_IMAGE_URL_PREFIX = "images/tempBookImages/";
    
    @Autowired
    private ServletContext servletContext;
    
    @Autowired
    private BookRepository bookRepository;
    
    @Autowired
    private TypeRepository typeRepository;
    
    @Autowired
    private MultipartFileService multipartFileService;
    
    @Override
    public Book createNewBook(BookCreationDTO bookCreationDTO) throws TypeNotFoundException {
        Book book = createBookFromBookCreationDTO(bookCreationDTO);
        if(book  == null) {
            return null;
        }
        book = bookRepository.add(book);
        return book;
    }
    
    @Override
    public boolean checkBookDownloadLink(BookDownloadLink bookDownloadLink) {
        return bookDownloadLink.getLink().matches(BOOK_DOWNLOAD_URL_REGEX);
    }
    
    @Override
    public void deleteBookById(int bookId, int userId) throws UserIdNotMatchesException {
        Book book = bookRepository.findOne(bookId);
        if(book.getUser().getUserId() != userId) {
            throw new UserIdNotMatchesException();
        }
        bookRepository.remove(book);
    }
    
    @Override
    public Book getBookById(int bookId) {
        return bookRepository.findOne(bookId);
    }
    
    @Override
    public Book updateBook(Book book, BookUpdateDTO BookUpdateDTO) throws TypeNotFoundException {
        String oldBookCoverImageUrl = book.getThumbnailUrl();
        
        List<String> imageUrlBeforeUpdate = new ArrayList<>();
        for(BookImage bookImage : book.getBookImages()) {
            if(bookImage != null) {
                imageUrlBeforeUpdate.add(bookImage.getImageURL());
            }
        }
        
        book = updateBookByBookUpdateDTO(book, BookUpdateDTO);
        book = bookRepository.save(book);
        
        String newBookCoverImageUrl = book.getThumbnailUrl();
        if(!oldBookCoverImageUrl.equals(newBookCoverImageUrl)) {
           removeBookCoverImageByBookImageUrl(oldBookCoverImageUrl);
        }
        
        List<String> imageUrlAfterUpdate = new ArrayList<>();
        for(BookImage bookImage : book.getBookImages()) {
            if(bookImage != null) {
                imageUrlAfterUpdate.add(bookImage.getImageURL());
            }
        }
        
        for(String s : imageUrlBeforeUpdate) {
            System.out.println("Old image url: " + s);
        }
        
        for(String s : imageUrlAfterUpdate) {
            System.out.println("New image url: " + s);
        }
        handeBookImageChange(imageUrlBeforeUpdate, imageUrlAfterUpdate);
        return book;
    }
    
    private void handeBookImageChange(List<String> imageUrlBeforeUpdateList, List<String> imageUrlAfterUpdateList) {
        for(int i = 0; i < imageUrlBeforeUpdateList.size(); i++) {
            String imageUrlBeforeUpdate = imageUrlBeforeUpdateList.get(i);
            if(i < imageUrlAfterUpdateList.size()) {
                String imageUrlAfterUpdate = imageUrlAfterUpdateList.get(i);
                if(!imageUrlBeforeUpdate.equals(imageUrlAfterUpdate)) {
                    removeBookImageByBookImageUrl(imageUrlBeforeUpdate);
                } 
            } else {
                removeBookImageByBookImageUrl(imageUrlBeforeUpdate);
            }
        }
    }
    
    @Override
    public BookCreationDTO getBookCreationDTO() {
        BookCreationDTO bookCreationDTO = new BookCreationDTO();
        bookCreationDTO.setBookDownloadLinks(fullFillBookDownloadLinkList(bookCreationDTO.getBookDownloadLinks()));
        bookCreationDTO.setBookImages(fullFillBookImageList(bookCreationDTO.getBookImages()));
        bookCreationDTO.setBookType(-1);
        return bookCreationDTO;
    }
    
    @Override
    public BookUpdateDTO getBookUpdateDTO(Book book) {
        book = Objects.requireNonNull(book, "Book must not be null");
        BookUpdateDTO bookUpdateDTO = new BookUpdateDTO();
        bookUpdateDTO.setBookId(book.getBookId());
        bookUpdateDTO.setBookName(book.getBookName());
        bookUpdateDTO.setAuthorName(book.getAuthor());
        bookUpdateDTO.setDescription(book.getBook_descripton());
        bookUpdateDTO.setBookType(book.getType().get(0).getTypeId());
        bookUpdateDTO.setBookThumbnailImageUrl(book.getThumbnailUrl());
        bookUpdateDTO.setBookDownloadLinks(book.getBookDowloadLinks());
        bookUpdateDTO.setBookImages(book.getBookImages());
        bookUpdateDTO.setBookImages(fullFillBookImageList(bookUpdateDTO.getBookImages()));
        bookUpdateDTO.setBookDownloadLinks(fullFillBookDownloadLinkList(bookUpdateDTO.getBookDownloadLinks()));
        return bookUpdateDTO;
    }
    
    @Override
    public BookCreationDTO handleBookCreationDTOAfterValidFalse(BookCreationDTO bookCreationDTO) {
        int fakeBookId = (int)(Math.random() * ((10 - 1) + 1)) + 1;
        bookCreationDTO.setBookId(fakeBookId);
        
        if(!bookCreationDTO.getBookThumbnailImage().isEmpty()) {        
            String tmpImagePath = handleTempBookImage(bookCreationDTO.getBookThumbnailImage());
            bookCreationDTO.setBookThumbnailImageUrl(tmpImagePath);
        }
        
        List<BookImage> bookImageList = bookCreationDTO.getBookImages();
        bookCreationDTO.setBookImages(fullFillBookImageList(bookCreationDTO.getBookImages()));
        for(BookImage bookImage : bookImageList) {
            System.out.println("Before handleBookCreationDTOAfterValidFalse: " + bookImage.getId());
            System.out.println("Before handleBookCreationDTOAfterValidFalse: " + bookImage.getImageURL());
        }
        int index = 0;
        for(MultipartFile bookImageFile : bookCreationDTO.getBookImagesMultipartFile()) {
            if(!bookImageFile.isEmpty()) {
                String tmpImageUrl = handleTempBookImage(bookImageFile);
                bookImageList.get(index).setImageURL(tmpImageUrl);
                index += 1;
            }
        }
        bookCreationDTO.setBookImages(bookImageList);
        bookCreationDTO.setBookDownloadLinks(fullFillBookDownloadLinkList(bookCreationDTO.getBookDownloadLinks()));
        return bookCreationDTO;
    }
    
    @Override
    public BookUpdateDTO handleBookUpdateDTOAfterValidFalse(BookUpdateDTO bookUpdateDTO, Book book) {
        if(bookUpdateDTO == null) {
            return null;
        }

        if(!bookUpdateDTO.getBookThumbnailImageUrl().isEmpty()) {        
            String tmpImagePath = handleTempBookImage(bookUpdateDTO.getBookThumbnailImage());
            bookUpdateDTO.setBookThumbnailImageUrl(tmpImagePath);
        } else {
            if(bookUpdateDTO.getBookThumbnailImageUrl() == null){
                bookUpdateDTO.setBookThumbnailImageUrl(book.getThumbnailUrl());
            }
        }
        
        List<BookImage> bookImageList;
        if(!bookUpdateDTO.getBookImages().isEmpty()) {
            bookImageList = bookUpdateDTO.getBookImages();
        } else {
            bookImageList = book.getBookImages();
        }
        bookImageList = fullFillBookImageList(bookImageList);
        bookImageList = resolveNewBookImageUserUploaded(bookImageList, bookUpdateDTO.getBookImagesMultipartFile());
        bookImageList = resolveUpdateBookImageUserUploaded(bookImageList, bookUpdateDTO.getBookUpdateImagesMultipartFile(), bookUpdateDTO.getImageUpdateIds());
        bookImageList = resolveBookImageDeleted(bookImageList, bookUpdateDTO.getImageRemovedIds());
  
        bookUpdateDTO.setBookImages(bookImageList);
        bookUpdateDTO.setBookDownloadLinks(fullFillBookDownloadLinkList(bookUpdateDTO.getBookDownloadLinks()));

        return bookUpdateDTO;
    }
    
    @Override
    public List<BookDTO> searchBooks(int max, String searchKey) {
        searchKey = HtmlUtils.htmlEscape(searchKey);
        return bookRepository.searchBookWithLimit(max, searchKey);
    }
    
    @Override
    public List<BookDTO> pagination(int amountOfBooksPerPage, int currentPage) {
        return pagination(amountOfBooksPerPage, currentPage, -1);
    }
    
    @Override
    public List<BookDTO> pagination(int amountOfBooksPerPage, int currentPage, int userId) {
        int firstResultIndex = (currentPage - 1) * amountOfBooksPerPage;
        List<BookDTO> booksSearched = new ArrayList<>();
        booksSearched = bookRepository.findAll_LiteVersion(amountOfBooksPerPage, firstResultIndex, userId);
        makeBeautifulDate(booksSearched);
        return booksSearched;
    }
    
    @Override
    public List<BookDTO> pagination(Properties paginationProperties, int userId) {
        List<BookDTO> booksSearched = new ArrayList<>();
        String orderBy = paginationProperties.getProperty("orderBy");
        paginationProperties = makePaginationPropertiesSuitableForBookRepo(paginationProperties);
        BookRepository.BooksSelectedOrderBy booksSelectedOrderBy = BookRepository.BooksSelectedOrderBy.BOOK_VIEW;
        
        switch(orderBy) {
            case "view":
                break;
            case "created_date":
                booksSelectedOrderBy = BookRepository.BooksSelectedOrderBy.BOOK_POSTED_DATE;
                break;
            default:
        }

        booksSearched = bookRepository.findAll_LiteVersion(paginationProperties, booksSelectedOrderBy, userId);
        makeBeautifulDate(booksSearched);
        return booksSearched;
    }
    
    @Override
    public int amountOfBooks() {
        return amountOfBooks(-1);
    }
    
    @Override
    public int amountOfBooks(int userId) {
        Long amountOfBooks = bookRepository.getAmountOfBooks(userId);
        return amountOfBooks.intValue();
    }
    
    @Override
    public List<BookDTO> searchBooks(Properties searchProperties) {
        List<BookDTO> booksSearched = new ArrayList<>();
        booksSearched = searchBooks(searchProperties, -1);
        makeBeautifulDate(booksSearched);
        return booksSearched;
    }
    
    @Override
    public List<BookDTO> searchBooks(Properties searchProperties, int userId) {
        List<BookDTO> booksSearched = new ArrayList<>();
        String orderBy = HtmlUtils.htmlEscape(searchProperties.getProperty("orderBy"));
        BookRepository.BooksSelectedOrderBy booksSelectedOrderBy = BookRepository.BooksSelectedOrderBy.BOOK_VIEW;
        
        switch (orderBy) {
            case "view":
                break;
            case "created_date":
                booksSelectedOrderBy = BookRepository.BooksSelectedOrderBy.BOOK_POSTED_DATE;
                break;  
            default:
        }
        
        searchProperties = makeSearchPropertiesSuitableForBookRepo(searchProperties);
        booksSearched = bookRepository.searchBookWithSearchPropertiesAndOrderBy(
            searchProperties,
            booksSelectedOrderBy,
            userId
        );
        makeBeautifulDate(booksSearched);
        return booksSearched;
    }
    
    @Override
    public int amountsOfSearchedResults(String searchKey) {
        return amountsOfSearchedResults(searchKey, -1);
    }
    
    @Override
    public List<BookDTO> getAllBookNotApproveWithPagination (int amountOfBooksPerPage, int currentPage) {
        int firstResultIndex = (currentPage - 1) * amountOfBooksPerPage;
        List<BookDTO> booksNotAprroveList = new ArrayList<>();
        booksNotAprroveList = bookRepository.findAllBooksNotAprrove(amountOfBooksPerPage, firstResultIndex);
        makeBeautifulDate(booksNotAprroveList);
        return booksNotAprroveList;
    }
    
    @Override
    public int amountsOfSearchedResults(String searchKey, int userId) {
        if(searchKey == null) {
            return 0;
        }
        
        searchKey = HtmlUtils.htmlEscape(searchKey);
        
        if(searchKey.isEmpty()) {
            return 0;
        }
        Long amountOfBooks = bookRepository.getAmountOfBooks(searchKey, userId);
        return amountOfBooks.intValue();
    }
    
    @Override
    public boolean isBookApproved(Book book) {
        return book.getStatusCode() == HibernateBookRepository.BOOK_APPROVED_STATUS_CODE;
    }
    
    @Override
    public Book bookApproval(Book book) {
        book = Objects.requireNonNull(book, "book must not be null");
        int bookApprovedStatusCode = HibernateBookRepository.BOOK_APPROVED_STATUS_CODE;
        book.setStatusCode(bookApprovedStatusCode);
        return bookRepository.save(book);
    }
    
    
    
    private Properties makePaginationPropertiesSuitableForBookRepo(Properties paginationProperties) {
        paginationProperties = makePropertiesSuitableForBookRepo(paginationProperties);
        return paginationProperties;
    }
    
    private Properties makeSearchPropertiesSuitableForBookRepo(Properties searchProperties) {
        searchProperties = makePropertiesSuitableForBookRepo(searchProperties);
        String searchKey = HtmlUtils.htmlEscape(searchProperties.getProperty("searchKey"));
        searchProperties.setProperty("searchKey", searchKey);
        return searchProperties;
    }
    
    private Properties makePropertiesSuitableForBookRepo(Properties properties) {
        int maxResult = Integer.parseInt(properties.getProperty("maxResults"));
        int page = Integer.parseInt(properties.getProperty("page"));
        int firstResultIndex = (page - 1) * maxResult;
        properties.setProperty("firstResultIndex", String.valueOf(firstResultIndex));
        properties.remove("page");
        properties.remove("orderBy");
        return properties;
    }
    
    
    private Book createBookFromBookCreationDTO(BookCreationDTO bookCreationDTO) throws TypeNotFoundException {
        Book book = new Book();
        List<Integer> typeIds = new ArrayList();
        List<Type> types = new ArrayList();
        typeIds.add(bookCreationDTO.getBookType());
        types = typeRepository.findTypesByIds(typeIds);
        if(types.size() == typeIds.size()) {
            book.setTypes(types);
        } else {
            throw new TypeNotFoundException();
        }
        
        String thumbnailUrl = handleBookCoverImage(bookCreationDTO.getBookThumbnailImage());
        if(!thumbnailUrl.isEmpty()) {
            book.setThumbnailUrl(thumbnailUrl); 
        } else {
            if(!bookCreationDTO.getBookThumbnailImageUrl().isEmpty()) {
                book.setThumbnailUrl(changeTempBookCoverImageToBookCoverImage(bookCreationDTO.getBookThumbnailImageUrl()));
            } else {
                return null;
            }
        }
        List<BookImage> bookImageList = book.getBookImages();
        if(!bookCreationDTO.getBookImages().isEmpty()) {
            bookImageList = bookCreationDTO.getBookImages();
        }
        bookImageList = removeUnusedBookImageFromBookImageList(bookImageList);
        
        for(BookImage bookImage : bookImageList) {
            System.out.println("Before createBookFromBookCreationDTO: " + bookImage.getId());
            System.out.println("Before createBookFromBookCreationDTO: " + bookImage.getImageURL());
        }
        bookImageList = addNewToBookImageList(bookImageList, bookCreationDTO.getBookImagesMultipartFile());
        bookImageList = changeTempBookImageInBookImageListToBookImage(bookImageList);
        
        book.setBookName(bookCreationDTO.getBookName());
        book.setAuthor(bookCreationDTO.getAuthorName());
        book.setBook_descripton(bookCreationDTO.getDescription());
        book.setUser(bookCreationDTO.getUser());
        book.setPostDate(new Date());
        book.setBookView(0);
        book.setBookImages(bookImageList);
        for(BookDownloadLink link : bookCreationDTO.getBookDownloadLinks()) {
            if(!link.getLink().isEmpty()) {
                book.addDownloadLink(link); 
            }
        }
        return book;
    }
    
    private Book updateBookByBookUpdateDTO(Book book, BookUpdateDTO bookUpdateDTO) throws TypeNotFoundException {
        List<Integer> typeIds = new ArrayList();
        List<Type> types = new ArrayList();
        typeIds.add(bookUpdateDTO.getBookType());
        types = typeRepository.findTypesByIds(typeIds);
        if(types.size() == typeIds.size()) {
            book.setTypes(types);
        } else {
            throw new TypeNotFoundException();
        }
        
        if(bookUpdateDTO.getBookThumbnailImageUrl() != null) {
            book.setThumbnailUrl(changeTempBookCoverImageToBookCoverImage(bookUpdateDTO.getBookThumbnailImageUrl()));
        } else if(!bookUpdateDTO.getBookThumbnailImage().isEmpty()) {
            String newBookCoverImageUrl = handleBookCoverImage(bookUpdateDTO.getBookThumbnailImage());
            if(newBookCoverImageUrl != null && !newBookCoverImageUrl.isEmpty()) {
                book.setThumbnailUrl(newBookCoverImageUrl);
            }
        }
        
        List<BookImage> bookImageList;
        if(!bookUpdateDTO.getBookImages().isEmpty()) {
            bookImageList = bookUpdateDTO.getBookImages();
        } else {
            bookImageList = book.getBookImages();
        }
        
        bookImageList = removeUnusedBookImageFromBookImageList(bookImageList);
        for(BookImage bookImage : bookImageList) {
            System.out.println("Before updateBookByBookUpdateDTO: " + bookImage.getId());
            System.out.println("Before updateBookByBookUpdateDTO: " + bookImage.getImageURL());
        }
        
        bookImageList = deleteBookImageFromBookImageList(bookImageList, bookUpdateDTO.getImageRemovedIds());
        bookImageList = updateBookImageList(bookImageList, bookUpdateDTO.getBookUpdateImagesMultipartFile(), bookUpdateDTO.getImageUpdateIds());
        bookImageList = addNewToBookImageList(bookImageList, bookUpdateDTO.getBookImagesMultipartFile());
        bookImageList = changeTempBookImageInBookImageListToBookImage(bookImageList);
        bookImageList = removeFakeIdFromBookImageList(book.getBookImages(), bookImageList);
        
        book.setBookName(bookUpdateDTO.getBookName());
        book.setAuthor(bookUpdateDTO.getAuthorName());
        book.setBook_descripton(bookUpdateDTO.getDescription());

        book.setBookImages(bookImageList);
        handleUpdatedBookImageDownloadLink(book, bookUpdateDTO);
        return book;
    }
    
    private List<BookImage> addNewToBookImageList(List<BookImage> bookImagesList, MultipartFile[] bookImagesMultipartFile) {
        if(bookImagesMultipartFile == null) return bookImagesList;
        String bookImageUrl;
        BookImage bookImage;
        for(MultipartFile bookImageMutilpartFile : bookImagesMultipartFile) {
            if(!bookImageMutilpartFile.isEmpty()) {
                bookImageUrl = handleBookImage(bookImageMutilpartFile);
                if(!bookImageUrl.isEmpty()) {
                    bookImage = new BookImage();
                    bookImage.setImageURL(bookImageUrl);
                    bookImagesList.add(bookImage);
                    System.out.println("Book Update Image Handle: Add new image");
                }
            }
        }
        return bookImagesList;
    }
    
    private List<BookImage> removeFakeIdFromBookImageList(List<BookImage> originalBookImagesList, List<BookImage> changedBookImageList) {
        List<Integer> bookBeforeUpdateIds = new ArrayList<>();
        originalBookImagesList.forEach((bookImage -> bookBeforeUpdateIds.add(bookImage.getId())));
        for(BookImage changedImage : changedBookImageList) {
           if(!bookBeforeUpdateIds.contains(changedImage.getId())) {
               changedImage.setId(0);
           }
        }
        return changedBookImageList;
    }
    
    private List<BookImage> updateBookImageList(List<BookImage> bookImagesList, MultipartFile[] bookImagesMultipartFile, int[] bookImageUpdateIds) {
        if(bookImagesMultipartFile == null || bookImageUpdateIds == null) return bookImagesList;
        String bookImageUrl;
        int index = 0;
        for(MultipartFile bookImageMutilpartFile : bookImagesMultipartFile) {
            if(!bookImageMutilpartFile.isEmpty()) {
                bookImageUrl = handleBookImage(bookImageMutilpartFile);
                for(BookImage bookImage : bookImagesList) {
                    if(bookImageUpdateIds[index] == bookImage.getId()) {
                        System.out.println("Book Update Image Handle: Update image-" + bookImage.getId());
                        bookImage.setImageURL(bookImageUrl);
                    }
                }
            }
            index += 1;
        }
        return bookImagesList;
    }
    
    private List<BookImage> deleteBookImageFromBookImageList(List<BookImage> bookImagesList, int[] removeIds) {
        if(removeIds == null) {
            return bookImagesList;
        }
        
        List<BookImage> bookImagesAfterDelete = new ArrayList<>();
        List<Integer> removeIdList = new ArrayList<>();
        for(int id : removeIds) {
            System.out.println("Book Update Image Handle: Delete image-" + id);
            removeIdList.add(id);
        }
        Stream<BookImage> stream = bookImagesList.stream();
        Stream<BookImage> filtered = stream.filter(bookImage -> !removeIdList.contains(bookImage.getId()));
        filtered.forEach((bookImage -> bookImagesAfterDelete.add(bookImage)));
        return bookImagesAfterDelete;
    }
    
    private Book handleUpdatedBookImageDownloadLink(Book book, BookUpdateDTO bookUpdateDTO) {
        List<BookDownloadLink> bookDownloadLinkList = book.getBookDowloadLinks();
        List<BookDownloadLink> newBookDownloadLinkList = bookUpdateDTO.getBookDownloadLinks();
        BookDownloadLink newbookDownloadLink = null;
        for(int i = 0; i < newBookDownloadLinkList.size(); i++) {
            newbookDownloadLink = newBookDownloadLinkList.get(i);
            if(bookDownloadLinkList.size() > i) {
                if(!newbookDownloadLink.getLink().isEmpty()) {
                    book.updateDownloadLink(i, newbookDownloadLink);
                } else {
                    book.removeDownloadLink(i);
                }
            } else {
                if(!newbookDownloadLink.getLink().isEmpty()) {
                    book.addDownloadLink(newbookDownloadLink);
                    newbookDownloadLink = null;
                }
            }
        }
        return book;
    }
    
    private List<BookImage> removeUnusedBookImageFromBookImageList(List<BookImage> bookImageList) {
        if(bookImageList.isEmpty()) return bookImageList;
        List<BookImage> bookImageListAfterRemoveUnusedValue = new ArrayList<>();
        Stream<BookImage> stream = bookImageList.stream();
        Stream<BookImage> filtered = stream.filter(bookImage -> bookImage.getId() > 0 || bookImage.getImageURL() != null);
        filtered.forEach((bookImage -> bookImageListAfterRemoveUnusedValue.add(bookImage)));
        return bookImageListAfterRemoveUnusedValue;
    }
    
    private List<BookImage> resolveNewBookImageUserUploaded(List<BookImage> currentBookImage, MultipartFile[] imagesUserUploaded) {
        if(imagesUserUploaded == null) {
            return currentBookImage;
        }
        List<BookImage> bookImageUnusedList = new ArrayList<>();
        Stream<BookImage> stream = currentBookImage.stream();
        Stream<BookImage> filtered = stream.filter(bookImage -> bookImage.getId() == 0);
        filtered.forEach((bookImage -> bookImageUnusedList.add(bookImage)));
        
        //create fake id for new book image uploaded
        int fakeBookImageId = 0;
        for(BookImage bookImage : currentBookImage) {
           fakeBookImageId += bookImage.getId();
        }
        int index = 0;
        for(MultipartFile bookImageFile : imagesUserUploaded) {
            if(!bookImageFile.isEmpty()) {
                fakeBookImageId += 1;
                String tmpImageUrl = handleTempBookImage(bookImageFile);
                bookImageUnusedList.get(index).setId(fakeBookImageId);
                bookImageUnusedList.get(index).setImageURL(tmpImageUrl);
            }
        }
        return currentBookImage;
    }
    
    private List<BookImage> resolveUpdateBookImageUserUploaded(List<BookImage> currentBookImage, MultipartFile[] imagesUserUploaded, int[] idsOfBookImageNeedUpdate) {
        if(imagesUserUploaded == null || idsOfBookImageNeedUpdate == null) {
            return currentBookImage;
        }
        int index = 0;
        for(MultipartFile bookImageFile : imagesUserUploaded) {
            if(!bookImageFile.isEmpty()) {
                String tmpImageUrl = handleTempBookImage(bookImageFile);
                for(BookImage bookImage : currentBookImage) { 
                    if(bookImage.getId() == idsOfBookImageNeedUpdate[index]) {
                        bookImage.setImageURL(tmpImageUrl);
                        break;
                    }
                }
            }
            index += 1;
        }
        return currentBookImage;
    }
    
    private List<BookImage> resolveBookImageDeleted(List<BookImage> currentBookImage, int[] idsOfDeletedBookImage) {
        if(idsOfDeletedBookImage == null) {
            return currentBookImage;
        }
        for(int removedImageId : idsOfDeletedBookImage) {
            for(BookImage bookImage : currentBookImage) {
                if(bookImage == null) {
                    continue;
                }
                if(bookImage.getId() == removedImageId) {
                    bookImage.setImageURL(null);
                    break;
                }
            }
        }
        return currentBookImage;
    }
    
    private List<BookImage> fullFillBookImageList(List<BookImage> currentBookImage) {
        for(int i = currentBookImage.size(); i < NUMBERS_OF_BOOK_IMAGES_USER_CAN_UPLOAD; i++) {
            currentBookImage.add(new BookImage());
        }
        return currentBookImage;
    }
    
    private List<BookDownloadLink> fullFillBookDownloadLinkList(List<BookDownloadLink> currentDownloadLinkList) {
        for(int i = currentDownloadLinkList.size(); i < NUMBERS_OF_BOOK_DOWNLOAD_LINKS_USER_CAN_CREATE; i++) {
           currentDownloadLinkList.add(new BookDownloadLink());
        }
        return currentDownloadLinkList;
    }
    
    private String handleTempBookImage(MultipartFile multipartFile) {
        String fileName = multipartFileService.storeTempBookImage(multipartFile);
        return TEMP_BOOK_IMAGE_URL_PREFIX + fileName;
    }
    
    private String handleBookCoverImage(MultipartFile multipartFile) {
        String fileName = multipartFileService.storeBookCoverImage(multipartFile);
        return BOOK_COVER_IMAGE_URL_PREFIX + fileName;
    }
        
    private String handleBookImage(MultipartFile multipartFile) {
        String fileName = multipartFileService.storeBookImage(multipartFile);
        return BOOK_IMAGE_URL_PREFIX + fileName;
    }
    
    private String changeTempBookCoverImageToBookCoverImage(String tempBookCoverImageUrl) {
        if(!tempBookCoverImageUrl.contains(TEMP_BOOK_IMAGE_URL_PREFIX)) {
            return tempBookCoverImageUrl;
        }
        String fileName = getImageNameFromUrl(tempBookCoverImageUrl);
        return BOOK_COVER_IMAGE_URL_PREFIX + multipartFileService.moveBookCoverImageFromTempStorageToMainStorage(fileName);
    }
    
    private String changeTempBookImageToBookImage(String tempBookImageUrl) {
        if(!tempBookImageUrl.contains(TEMP_BOOK_IMAGE_URL_PREFIX)) {
            return tempBookImageUrl;
        }
        String fileName = getImageNameFromUrl(tempBookImageUrl);
        return BOOK_IMAGE_URL_PREFIX + multipartFileService.moveBookImageFromTempStorageToMainStorage(fileName);
    }
    
    private void removeBookImageByBookImageUrl(String bookImageUrl) {
        multipartFileService.removeBookImage(bookImageUrl);
    }
    
    private void removeBookCoverImageByBookImageUrl(String bookCoverImageUrl) {
        multipartFileService.removeBookCoverImage(bookCoverImageUrl);
    }
    
    private List<BookImage> changeTempBookImageInBookImageListToBookImage( List<BookImage> bookImageList) {
        String newUrl = "";
        for(BookImage bookImage : bookImageList) {
            if(bookImage.getImageURL().contains(TEMP_BOOK_IMAGE_URL_PREFIX)) {
                newUrl = changeTempBookImageToBookImage(bookImage.getImageURL());
                bookImage.setImageURL(newUrl);
            }
        }
        return bookImageList;
    }
    
    private String getImageNameFromUrl(String bookImageUrl) {
        if(bookImageUrl != null && !bookImageUrl.isEmpty()) {
            return bookImageUrl.substring(bookImageUrl.lastIndexOf("/") + 1); 
        }
        return bookImageUrl;
    }
    
    private void makeBeautifulDate(List<BookDTO> bookDTOs) {
        bookDTOs.forEach(
            bookDTO -> bookDTO.setMeaningfulPostDate(DateUtils.getMeaningfulDate(bookDTO.getPostDate()))
        );
    }
}
