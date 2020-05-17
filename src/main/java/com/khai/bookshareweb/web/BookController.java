/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.khai.bookshareweb.web;


import com.khai.bookshareweb.data.BookRepository;
import com.khai.bookshareweb.data.HibernateBookRepository;
import com.khai.bookshareweb.data.JpaBookRepository;
import com.khai.bookshareweb.data.TypeRepository;
import com.khai.bookshareweb.dto.BookCreationDTO;
import com.khai.bookshareweb.dto.BookImageDTO;
import com.khai.bookshareweb.dto.BookUpdateDTO;
import com.khai.bookshareweb.dto.TempBookImageListDTO;
import com.khai.bookshareweb.entity.Book;
import com.khai.bookshareweb.entity.BookDownloadLink;
import com.khai.bookshareweb.entity.BookImage;
import com.khai.bookshareweb.entity.Type;
import com.khai.bookshareweb.entity.User;
import com.khai.bookshareweb.service.BookService;
import com.khai.bookshareweb.service.SecurityService;
import com.khai.bookshareweb.service.exception.TypeNotFoundException;
import com.khai.bookshareweb.service.exception.UserIdNotMatchesException;
import java.io.File;
import java.util.ArrayList;
import java.util.List;
import javax.servlet.ServletContext;
import javax.servlet.http.HttpSession;
import javax.validation.Valid;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

/**
 *
 * @author Acer
 */

@Controller
@RequestMapping(value = "/book/*")
public class BookController {
    
    private static final Log LOGGER = LogFactory.getLog(BookController.class);
    
    private static final String TEMP_IMAGE_LIST_SESSION_ATTRIBUTE_NAME = "TempBookImageList";
    private static final String BOOK_CREATION_TEMP_THUMBNAIL_IMAGE_SESSION_ATTRIBUTE_NAME = "TempBookThumbnailImage";
    private static final String BOOK_UPDATE_TEMP_THUMBNAIL_IMAGE_SESSION_ATTRIBUTE_NAME = "TempBookThumbnailImage";
    private static final String TEMP_IMAGE_LIST_OF_CREATION_SESSION_ATTRIBUTE_NAME = "CreationBookTempImageList";
    private static final String AUTO_REMOVE_IMAGE_LIST_SESSION_ATTRIBUTE_NAME = "TempBookImageListToRemove";
    
    @Autowired
    private BookService bookService;
    
    @Autowired
    private SecurityService securityService;
        
    @Autowired
    private TypeRepository typeRepository;
    
    @Autowired
    private ServletContext servletContext;
    
    @RequestMapping(value="{bookId}/*", method=RequestMethod.GET)
    public String book(@PathVariable int bookId, Model model) {
        Book book = bookService.getBookById(bookId);
        System.out.println(book.getTypes().size());
        model.addAttribute("book", book);
        return "book";
    }
    
    @RequestMapping(value="/delete-book", method = RequestMethod.GET)
    public String deleteBook(HttpSession session, Model model, @RequestParam(name = "_id") int bookId) {
        User user = securityService.getCurrentLoggedUserInSession(session);
        try {
            bookService.deleteBookById(bookId, user.getUserId());
        } catch (UserIdNotMatchesException ex) {
            return "redirect:/error/404";
        }
        return "redirect:/user/book-posted";
    }
    
    @RequestMapping(value="/create-new-book", method = RequestMethod.GET)
    public String addNewBook(HttpSession session, Model model) {
        removeBookCreateDTOSessionAttribute(session);
        List<Type> bookTypes = typeRepository.findAll();
        BookCreationDTO bookCreationDTO = bookService.getBookCreationDTO();
        model.addAttribute("bookTypes", bookTypes);
        model.addAttribute("addBookDto", bookCreationDTO);
        return "book/createNewBook";
    }
    
    @RequestMapping(value="/update-book", method = RequestMethod.GET)
    public String updateBook(HttpSession session, Model model, @RequestParam(name = "_id") int bookId) {
        Book book = bookService.getBookById(bookId);
        if(book == null) {
            return "redirect:/error/404";
        }
        removeBookUpdateDtoSessionAttribute(bookId, session);
        List<Type> bookTypes = typeRepository.findAll();
        BookUpdateDTO bookUpdateDTO = bookService.getBookUpdateDTO(book);
        model.addAttribute("bookTypes", bookTypes);
        model.addAttribute("bookUpdateDTO", bookUpdateDTO);
        return "book/updateBook";
    }
    
    
    @RequestMapping(value="/create-new-book", method = RequestMethod.POST)
    public String addNewBookHandle(@ModelAttribute("addBookDto") @Valid BookCreationDTO bookCreationDTO,
            BindingResult bindingResult,
            HttpSession session,
            Model model,
            RedirectAttributes redirectAttributes) {
        Book book = null;
        User user = securityService.getCurrentLoggedUserInSession(session);
        List<Type> bookTypes = typeRepository.findAll();
        model.addAttribute("user", user);
        model.addAttribute("bookTypes", bookTypes);

        List<BookImage> tempBookImageList = (List)session.getAttribute(TEMP_IMAGE_LIST_OF_CREATION_SESSION_ATTRIBUTE_NAME + "-" + bookCreationDTO.getBookId());
        if(tempBookImageList != null) {
            LOGGER.info("Get temp images info user uploaded from session");
            LOGGER.info("Size: " + tempBookImageList.size());
            bookCreationDTO.setBookImages(tempBookImageList);
        }
        String bookThumbnailImageUrl = (String)session.getAttribute(BOOK_CREATION_TEMP_THUMBNAIL_IMAGE_SESSION_ATTRIBUTE_NAME + "-" + bookCreationDTO.getBookId());
        if(bookThumbnailImageUrl != null) {
            bookCreationDTO.setBookThumbnailImageUrl(bookThumbnailImageUrl);
        }
        
        boolean isImagesAndDownLoadLinksIsValid = isImageAndDownloadLinksOfBookCreationDtoValid(bookCreationDTO, model, bindingResult);
        if(bindingResult.hasErrors() || !isImagesAndDownLoadLinksIsValid) {
            bookCreationDTO = bookService.handleBookCreationDTOAfterValidFalse(bookCreationDTO);
            storeTempBookImagesOfBookCreationDTOToSession(bookCreationDTO, session);
            addTempBookImageToAutoRemoveImageList(bookCreationDTO.getBookImages(), session);
            addTempBookCoverImageToAutoRemoveImageList(bookCreationDTO.getBookThumbnailImageUrl(), session);
            LOGGER.error("Create book failed - store temp images info user uploaded to session");
            return "book/createNewBook";
        }
        
        bookCreationDTO.setUser(user);
        
        try {
            book = bookService.createNewBook(bookCreationDTO);
        } catch(TypeNotFoundException ex) {
            LOGGER.error(ex.getMessage());
        }
        if(book == null) {
            LOGGER.error("Create new book failed");
            redirectAttributes.addFlashAttribute("handleFailed", true);
            return "redirect:/book/create-new-book";
        }
        removeBookCreateDTOSessionAttribute(session);
        redirectAttributes.addFlashAttribute("handleSuccess", true);
        return "redirect:/book/create-new-book";
    }
    
    private void storeTempBookImagesOfBookCreationDTOToSession(BookCreationDTO bookCreationDTO, HttpSession session) {
        session.setAttribute(TEMP_IMAGE_LIST_OF_CREATION_SESSION_ATTRIBUTE_NAME, bookCreationDTO.getBookImages());
        session.setAttribute(BOOK_CREATION_TEMP_THUMBNAIL_IMAGE_SESSION_ATTRIBUTE_NAME, bookCreationDTO.getBookThumbnailImageUrl());
    }
    
    private void removeBookCreateDTOSessionAttribute(HttpSession session) {
        session.removeAttribute(TEMP_IMAGE_LIST_OF_CREATION_SESSION_ATTRIBUTE_NAME);
        session.removeAttribute(BOOK_CREATION_TEMP_THUMBNAIL_IMAGE_SESSION_ATTRIBUTE_NAME);
    }
    
    @RequestMapping(value="/update-book", method = RequestMethod.POST)
    public String updateBookHandle(@ModelAttribute("bookUpdateDTO") @Valid BookUpdateDTO bookUpdateDTO,
            BindingResult bindingResult,
            HttpSession session,
            Model model,
            RedirectAttributes redirectAttributes) {
        User user = securityService.getCurrentLoggedUserInSession(session);
        if(user == null) {
            LOGGER.error("User not found");
            return "redirect:/error/404";
        }
        
        int bookId = bookUpdateDTO.getBookId();
        Book book = bookService.getBookById(bookId);
        if(book == null) {
            LOGGER.error("Book not found");
            return "redirect:/error/404";
        }
        
        if(book.getUser().getUserId() != user.getUserId()) {
            LOGGER.error("Book and user not match");
            return "redirect:/error/404";
        }
        
        List<BookImage> tempBookImageList = (List)session.getAttribute(TEMP_IMAGE_LIST_SESSION_ATTRIBUTE_NAME + "-" + String.valueOf(bookId));
        if(tempBookImageList != null) {
            LOGGER.info("Get temp images info user uploaded from session");
            LOGGER.info("Size: " + tempBookImageList.size());
            bookUpdateDTO.setBookImages(tempBookImageList);
        }
        String bookThumbnailImageUrl = (String)session.getAttribute(BOOK_UPDATE_TEMP_THUMBNAIL_IMAGE_SESSION_ATTRIBUTE_NAME + "-" + bookUpdateDTO.getBookId());
        if(bookThumbnailImageUrl != null) {
            bookUpdateDTO.setBookThumbnailImageUrl(bookThumbnailImageUrl);
        }
        
        boolean isImageAndDownloadLinksValid = isImageAndDownloadLinksOfBookUpdateDtoValid(bookUpdateDTO, model, bindingResult);
        
        if(bindingResult.hasErrors() || !isImageAndDownloadLinksValid) {
            List<Type> bookTypes = typeRepository.findAll();
            model.addAttribute("user", user);
            model.addAttribute("bookTypes", bookTypes);
            bookUpdateDTO = bookService.handleBookUpdateDTOAfterValidFalse(bookUpdateDTO, book);
            storeTempBookImagesOfBookUpdateDTOToSession(bookUpdateDTO, session);
            addTempBookImageToAutoRemoveImageList(bookUpdateDTO.getBookImages(), session);
            addTempBookCoverImageToAutoRemoveImageList(bookUpdateDTO.getBookThumbnailImageUrl(), session);
            LOGGER.error("Update book failed - store temp images info user uploaded to session");
            return "book/updateBook";
        }
        bookUpdateDTO.setUser(user);
        try {
            book = bookService.updateBook(book, bookUpdateDTO);
        } catch(TypeNotFoundException ex) {
            LOGGER.error("Type not found");
            return "redirect:/error/404";
        }

        if(book == null) {
            LOGGER.error("Update book failed");
            redirectAttributes.addFlashAttribute("handleFailed", true);
            return "redirect:/book/update-book" + "?_id=" + bookId;
        }
        removeBookUpdateDtoSessionAttribute(bookId, session);
        redirectAttributes.addFlashAttribute("handleSuccess", true);
        return "redirect:/book/update-book" + "?_id=" + book.getBookId();
    }
    
    
    private void storeTempBookImagesOfBookUpdateDTOToSession(BookUpdateDTO bookUpdateDTO, HttpSession session) {
        session.setAttribute(TEMP_IMAGE_LIST_SESSION_ATTRIBUTE_NAME +
            "-" + String.valueOf(bookUpdateDTO.getBookId()) , bookUpdateDTO.getBookImages());
        session.setAttribute(BOOK_UPDATE_TEMP_THUMBNAIL_IMAGE_SESSION_ATTRIBUTE_NAME +
            "-" + String.valueOf(bookUpdateDTO.getBookId()) , bookUpdateDTO.getBookThumbnailImageUrl());
    }
    
    private void removeBookUpdateDtoSessionAttribute(int bookId, HttpSession session) {
        session.removeAttribute(TEMP_IMAGE_LIST_SESSION_ATTRIBUTE_NAME + "-" + bookId);
        session.removeAttribute(BOOK_UPDATE_TEMP_THUMBNAIL_IMAGE_SESSION_ATTRIBUTE_NAME + "-" + bookId);
    }
    
    private boolean isImageAndDownloadLinksOfBookCreationDtoValid(BookCreationDTO bookCreationDTO, Model model, BindingResult bindingResult) {
        MultipartFile bookThumbnailImage = bookCreationDTO.getBookThumbnailImage();
        String bookThumbnailImageUrl =  bookCreationDTO.getBookThumbnailImageUrl();
        
        if(!isBookCoverImageValid(bookThumbnailImage) && bookThumbnailImageUrl.isEmpty()) {
            bindingResult.rejectValue("bookThumbnailImage", "", "Ảnh bìa không phù hợp");
            return false;
        } 
        String[] downloadLinkErrors = checkBookDownloadLinks(bookCreationDTO.getBookDownloadLinks());
        if(downloadLinkErrors != null) {
            model.addAttribute("downloadLinkErrors", downloadLinkErrors);
            return false;
        }
        return true;
    }
    
    private boolean isImageAndDownloadLinksOfBookUpdateDtoValid(BookUpdateDTO bookUpdateDTO, Model model, BindingResult bindingResult) {
        String[] downloadLinkErrors = checkBookDownloadLinks(bookUpdateDTO.getBookDownloadLinks());
        
        if(downloadLinkErrors != null) {
            model.addAttribute("downloadLinkErrors", downloadLinkErrors);
            return false;
        }
        return true;
    }
    
    private boolean isBookCoverImageValid(MultipartFile bookCoverImage) {
        return !bookCoverImage.isEmpty();
    }
    
    private String[] checkBookDownloadLinks(List<BookDownloadLink> bookDownloadLinks) {
        int count = 0;
        String link = "";
        String[] downloadLinkErrors = null;
        
        for(BookDownloadLink bookDownloadLink : bookDownloadLinks) {
            link = bookDownloadLink.getLink();
            //First link must not be empty
            if(count == 0) {
                if( link.isEmpty()) {
                    if(downloadLinkErrors == null) {
                       downloadLinkErrors = new String[bookDownloadLinks.size()]; 
                    }
                    System.out.println("Must have link error");
                    downloadLinkErrors[count] = "Link Download không phù hợp";
                }
                count += 1;
                continue;
            }
            if(!link.isEmpty() && !bookService.checkBookDownloadLink(bookDownloadLink)) {
                if(downloadLinkErrors == null) {
                   downloadLinkErrors = new String[bookDownloadLinks.size()]; 
                }
                System.out.println("Temp link error");
                downloadLinkErrors[count] = "Link Download không phù hợp";
                count += 1;
            }
        }
        return downloadLinkErrors;
    }
    
    private void addTempBookCoverImageToAutoRemoveImageList(String bookCoverImageUrl, HttpSession session) {
        if(bookCoverImageUrl!= null && isTempImage(bookCoverImageUrl)) {
            addImageFileNameToAutoRemoveImageList(bookCoverImageUrl, session);
        }
        
    }
      
    private void addTempBookImageToAutoRemoveImageList(List<BookImage> bookImageList, HttpSession session) {
        String fileName = "";
        for(BookImage bookImage : bookImageList) {
            if(bookImage.getImageURL() != null && isTempImage(bookImage.getImageURL())) {
                fileName = getImageNameFromUrl(bookImage.getImageURL());
                addImageFileNameToAutoRemoveImageList(fileName, session);
            }
        }
    }
    
    private boolean isTempImage(String imageUrl) {
        return imageUrl.contains("images/tempBookImages/");
    }
    
    private String getImageNameFromUrl(String bookImageUrl) {
        if(bookImageUrl != null && !bookImageUrl.isEmpty()) {
            return bookImageUrl.substring(bookImageUrl.lastIndexOf("/") + 1); 
        }
        return bookImageUrl;
    }

    private void addImageFileNameToAutoRemoveImageList(String bookImageFileName, HttpSession session) {
        TempBookImageListDTO imageAutoRemoveList = (TempBookImageListDTO)session.getAttribute(AUTO_REMOVE_IMAGE_LIST_SESSION_ATTRIBUTE_NAME);
        if(imageAutoRemoveList == null) {
            imageAutoRemoveList = new TempBookImageListDTO(new ArrayList(), servletContext);
            imageAutoRemoveList.addBookImageFileName(bookImageFileName);
            session.setAttribute(AUTO_REMOVE_IMAGE_LIST_SESSION_ATTRIBUTE_NAME , imageAutoRemoveList);
        }
        imageAutoRemoveList.addBookImageFileName(bookImageFileName);
    }
    
    
}
