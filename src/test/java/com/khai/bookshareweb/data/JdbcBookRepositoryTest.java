/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.khai.bookshareweb.data;

import com.khai.bookshareweb.common.DateUtils;
import com.khai.bookshareweb.dto.BookDTO;
import com.khai.bookshareweb.entity.Book;
import com.khai.bookshareweb.entity.BookDownloadLink;
import com.khai.bookshareweb.entity.BookImage;
import com.khai.bookshareweb.entity.BookType;
import com.khai.bookshareweb.entity.Type;
import com.khai.bookshareweb.entity.User;
import com.khai.bookshareweb.entity.UserAccount;
import com.khai.bookshareweb.entity.VerificationToken;
import com.khai.bookshareweb.service.UserService;
import java.io.File;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;
import java.util.UUID;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.util.StringUtils;

/**
 *
 * @author Acer
 */

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes=HibernateBookRepositoryConfig.class)
public class JdbcBookRepositoryTest {
//    @Autowired
//    private JdbcBookRepository jdbcBookRepo;
    
    @Autowired
    private BookRepository hibernateBookRepository;
    
    @Autowired
    private UserRepository hibernateUserRepository;
    
    @Autowired
    private TypeRepository hibernateTypeRepository;
    
    @Autowired
    private UserAccountRepository hibernateUserAccountRepository;
    
    @Autowired
    private VerificationTokenRepository verificationTokenRepository;
    
    @Autowired
    private JavaMailSender mailSender;
    
    @Autowired
    private MessageSource messageSource;
    
    @Autowired
    private BookImageRepository bookImageRepository;
    
//    @Test
//    public void testFindAll() {
//        List<Book> bookList = jdbcBookRepo.findAll();
//        Assert.notNull(bookList, "Null Book List");
//        Assert.notEmpty(bookList, "Empty Book List");
//        for(Book book : bookList) {
//            System.out.println(book.getBookName());
//        }
//    }
    
    @Test
    public void defaultTest() {
        
    }
    
//    @Test
    public void testBCryptEncode() {
        BCryptPasswordEncoder bCryptPasswordEncodernew = new BCryptPasswordEncoder();
        System.out.println(bCryptPasswordEncodernew.encode("123456789"));
    }
    
//    @Test
    public void testMessagesSource() {
        Locale l = StringUtils.parseLocale("vi_VN");
        if(l == null) {
            throw new NullPointerException();
        }
        System.out.println(l.getCountry());
        System.out.println(l.getDisplayCountry());
        System.out.println(messageSource.getMessage("form.register.error.userName.size", null, l));
        System.out.println("Mật khẩu");
    }
    
//    @Test
    public void testCreateUserAccount() {
        User user = new User();
        user.setUserName("s400");
        user.setEmail("s400@gmail.com");
        
        String password = new BCryptPasswordEncoder().encode("987654321");
        UserAccount userAccount = new UserAccount("mercedes", password, user, 2);
        user.setUserAccount(userAccount);
        user = hibernateUserRepository.save(user);
        
        String token = UUID.randomUUID().toString();
        
        VerificationToken verificationToken = createToken(user.getUserAccount(), token);
        
        verificationTokenRepository.save(verificationToken);
//        userAccount = hibernateUserAccountRepository.findByUsername("mercedes");
//        System.out.println(userAccount.getAccountName());
//        System.out.println(userAccount.getUser().getUserName());
    }
    
    
//    @Test
    public void testSpringEmail() {
        String subject = "Registration Confirmation";
        String recipientAddress = "khaimap2015@gmail.com";
        String confirmationUrl = "BookShareWeb" + "/regitrationConfirm.html?token=" + "aaaa";
        String message = "Vao link sau de xac nhan tai khoan: ";
        SimpleMailMessage email = new SimpleMailMessage();
        email.setSubject(subject);
        email.setText(message + "http://localhost:8080" + confirmationUrl);
        email.setTo(recipientAddress);
        mailSender.send(email);
    }
    
    private VerificationToken createToken(UserAccount userAccount, String token) {
        VerificationToken verificationToken = new VerificationToken();
        verificationToken.setToken(token);
        verificationToken.setUserAccount(userAccount);
        verificationToken.setExpiryDate(VerificationToken.calculateExpiryDate(VerificationToken.IN_MINUTES_EXPIRATION));
        return verificationToken;
    }
    
    
//    @Test
    public void testRemoveUser() {
        User user = hibernateUserRepository.findByEmail("khaimap2015@gmail.com");
        hibernateUserRepository.remove(user);
//        if(user != null) {
//            System.out.println(user.getUserName());
//        } 
    }
    
    
//    @Test
    public void testFindUser() {
        User user = hibernateUserRepository.findByEmail("s400@.com");
        if(user != null) {
            System.out.println(user.getUserName());
        } 
        
    }
    
//    @Test
    public void testFindBook() {
        int bookId = 57;
        Book book = hibernateBookRepository.findOne(bookId);
        if(book == null) {
            return;
        }
        System.out.println("Book is exist");
        System.out.println(book.getBookName());
        List<Type> types = book.getType();
        List<BookType> bookTypes = book.getTypes();
        for(Type type : types) {
            System.out.println(type.getTypeName());
        }
        
        for(BookType bookType : bookTypes) {
            System.out.println(bookType.getBook().getBookName());
        }
        System.out.println(book.getBookImages().size());
    }
    
//    @Test
    public void findAllBookNotApprove() {
        List<BookDTO> booksNotAprroveList = hibernateBookRepository.findAllBooksNotAprrove(20, 0);
        makeBeautifulDate(booksNotAprroveList);
        booksNotAprroveList.forEach(bookDto -> System.out.println(bookDto.getPostDate()));
        booksNotAprroveList.forEach(bookDto -> System.out.println(bookDto.getMeaningfulPostDate()));
    }
    
    private void makeBeautifulDate(List<BookDTO> bookDTOs) {
        bookDTOs.forEach(
            bookDTO -> bookDTO.setMeaningfulPostDate(DateUtils.getMeaningfulDate(bookDTO.getPostDate()))
        );
    }
    
//    @Test
    public void testGetAllBookImage() {
        List<BookImage> bookImageList = bookImageRepository.getAll();
        bookImageList.forEach(bookImage -> System.out.println(bookImage.getImageURL()));
    }
    
//    @Test
    public void testRemoveUnusedBookImage() {
        List<BookImage> bookImageList = bookImageRepository.getAll();
        List<String> usedBookImageFileName = new ArrayList<>();
        bookImageList.forEach(bookImage -> usedBookImageFileName.add(getImageNameFromUrl(bookImage.getImageURL())));
        
        List<String> usedBookThumbnailsFileName = new ArrayList<>();
        List<BookDTO> bookDTOList = hibernateBookRepository.findAll_LiteVersion();
        bookDTOList.forEach(bookDto -> usedBookThumbnailsFileName.add(getImageNameFromUrl(bookDto.getThumbnailUrl())));
        
        File imageStoragedDir = new File("C:\\Users\\Acer\\Documents\\NetBeansProjects\\BookShareWeb\\target\\BookShareWeb-1.0-SNAPSHOT\\public\\images\\thumbnails");
        for(File imageFile : imageStoragedDir.listFiles()) {
            if(!usedBookImageFileName.contains(imageFile.getName())) {
                if(!usedBookThumbnailsFileName.contains(imageFile.getName())) {
                    System.out.println(imageFile.getName());
                    imageFile.delete();
                }
            }
        }
        
    }
    
    private String getImageNameFromUrl(String bookImageUrl) {
        if(bookImageUrl != null && !bookImageUrl.isEmpty()) {
            return bookImageUrl.substring(bookImageUrl.lastIndexOf("/") + 1); 
        }
        return bookImageUrl;
    }
    
//    @Test
    public void testAddBookImageUrl() {
        Book book = hibernateBookRepository.findOne(33);
        int sum = 0;
        for(BookImage image : book.getBookImages()) {
            System.out.println(image.getImageURL());
            sum += image.getId();
        }
        BookImage image = new BookImage();
        image.setImageURL("abcd");
        image.setId(0);
        book.addImage(image);
        hibernateBookRepository.save(book);
        for(BookImage bookImage : book.getBookImages()) {
            System.out.println(image.getImageURL());
        }
    }
    
//    @Test
    public void testUpdateBookImageUrl() {
        Book book = hibernateBookRepository.findOne(33);
        for(BookImage image : book.getBookImages()) {
            System.out.println(image.getImageURL());
        }
        BookImage image = new BookImage();
        image.setImageURL("abcd");
        image.setId(book.getBookImages().get(0).getId());
        book.updateImage(image);
        hibernateBookRepository.save(book);
        for(BookImage bookImage : book.getBookImages()) {
            System.out.println(image.getImageURL());
        }
    }
    
//    @Test
    public void test_LiteVersionOfGetAllBooks() {
        List<BookDTO> books = hibernateBookRepository.findAll_LiteVersion();
        for(BookDTO b : books) {
            System.out.println(b.getBookName());
        }
    }
    
//    @Test
    public void testFindUserAccount() {
        UserAccount userAccount = hibernateUserAccountRepository.findByUsername("unknow");
        System.out.println(userAccount.getAccountName());
        System.out.println(userAccount.getUser().getUserName());
    }
    
//    @Test
    public void testFindUsersById() {
        User user = hibernateUserRepository.findOne(42);
        System.out.println(user.getUserId());
    }
    
//    @Test
    public void testFindBookByUserId() {
        List<Book> books = hibernateBookRepository.findByUserId(21);
        for(Book book : books) {
            System.out.println(book.getType().get(0).getTypeName());
        }
    }
    
//    @Test
    public void testGetBookByType() {
//        List<Book> books = hibernateTypeRepository.findOne(10).getBooksSameType();
//        for(Book book : books) {
//            System.out.println(book.getBookId());
//        }
        
        List<BookType> bookTypes = hibernateTypeRepository.findOne(10).getBooks();
        
        for(BookType bookType : bookTypes) {
            System.out.println(bookType.getType().getTypeName());
        }
    }
    
//    @Test
    public void testFindTypesByIds() {
        List<Integer> typeIds = new ArrayList<>();
        typeIds.add(10);
        typeIds.add(20);
        typeIds.add(30);
        
        List<Type> types = hibernateTypeRepository.findTypesByIds(typeIds);
        
        for(Type type : types) {
            System.out.println(type.getTypeId());
            System.out.println(type.getTypeName());
        }
    }
    
//    @Test
    public void testSearchBook() {
        List<BookDTO> bookDTOs = hibernateBookRepository.searchBookWithLimit(5, "java");
        for(BookDTO bookDTO : bookDTOs) {
            System.out.println(bookDTO.getBookName() + " - " + bookDTO.getBookView());
        }
    }
    
//    @Test
    public void testPaginnation() {
        List<BookDTO> bookDTOs = hibernateBookRepository.findAll_LiteVersion(30, 0);
        for(BookDTO bookDTO : bookDTOs) {
            System.out.println(bookDTO.getBookName() + " - " + bookDTO.getBookView());
        }
    }
    
//    @Test
    public void testGetAmountOfBook() {
        long amountOfBooks = hibernateBookRepository.getAmountOfBooks();
        System.out.println(amountOfBooks);
    }
    
//    @Test
    public void testUpdate() {
        Book book = hibernateBookRepository.findOne(86);
        
        List<Integer> typeIds = new ArrayList<>();

        typeIds.add(10);
//        typeIds.add(20);
//        typeIds.add(30);
        
        for(Type type : book.getType()) {
            System.out.println(type.getTypeId());
            System.out.println(type.getTypeName());
//            typeIds.add(type.getTypeId());
        }
        
        
//        ArrayList<Integer> ar = new ArrayList<>();
//        ar.retainAll(ar);
//        Set<Integer> s = new HashSet<>();
//        

        
        List<Type> types = hibernateTypeRepository.findTypesByIds(typeIds);
        
        System.out.println(types.size());
        System.out.println(book.getTypes().size());
        book.setTypes(types);
        
        System.out.println(book.getTypes().size());
        
        book = hibernateBookRepository.save(book);
       
    }
    
//    @Test
    public void testGetBookDownloadLink() {
        Book book = hibernateBookRepository.findOne(17);
        List<BookDownloadLink> bookDownloadLinks = book.getBookDowloadLinks();
        System.out.println(bookDownloadLinks.size());
        for(BookDownloadLink  bookDownloadLink : bookDownloadLinks) {
            System.out.println(bookDownloadLink.getLink());
            System.out.println(bookDownloadLink.getCreated());
            System.out.println(bookDownloadLink.getUpdated());
            System.out.println("------------------------------------------------");
        }
    }
    
//    @Test
    public void testAddBookDownloadLink() {
        Book book = hibernateBookRepository.findOne(64);
        List<BookDownloadLink> bookDownloadLinks = book.getBookDowloadLinks();
        System.out.println(bookDownloadLinks.size());
        for(BookDownloadLink  bookDownloadLink : bookDownloadLinks) {
            System.out.println(bookDownloadLink.getLink());
            System.out.println(bookDownloadLink.getCreated());
            System.out.println(bookDownloadLink.getUpdated());
            System.out.println("------------------------------------------------");
        }
        BookDownloadLink link = new BookDownloadLink();
        link.setLink("https://www.amazon.com/You-Dont-Know-JS-Closures/dp/1449335586/ref=bseries_primary_1_1449335586");
        book.addDownloadLink(link);
        hibernateBookRepository.save(book);
    }
    
    
//    @Test
    public void testUpdateBook() {
//        Type type = hibernateTypeRepository.findOne(50);
//        System.out.println("Size: " + type.getBooks().size());
//        for(BookType bookType : type.getBooks()) {
//            bookType.hashCode();
//        }
//        System.out.println("Size: " + type.getBooks().get(2).getBook().getBookId());       
        Book book = hibernateBookRepository.findOne(57);
//        System.out.println("Size: " + book.getTypes().get(0));
//        System.out.println("Size: " + book.getTypes().get(0).getType().getBooks().size());
//        System.out.println("Size: " + book.getTypes().get(0).getType().getTypeName());
//        System.out.println("Size: " + book.getTypes().get(1).getType().getTypeName());
//        User user = hibernateUserRepository.findOne(1);
//        System.out.println(user.getBooks().size());
        Book book2 = new Book();
        book2.setAuthor(book.getAuthor());
        book2.setPostDate(book.getPostDate());
        book2.setBookView(0);
        book2.setThumbnailUrl(book.getThumbnailUrl());
        book2.setUser(book.getUser());
        book2.setType(book.getType().get(0));
        book2.setBookName("no name");
//        System.out.println(book.getBookName());
//        System.out.println(book.getTypes().size());
//        System.out.println(book.getType().size());
//        book.setBookName("no name");
//        book = hibernateBookRepository.findOne(33);
//        System.out.println(book.getBookName());
//        System.out.println(book2.getBookId());
//        System.out.println(book2.getBookId());
//        book2.setBookName("the witcher");

//        hibernateBookRepository.remove(book2);
        hibernateBookRepository.save(book2);
//        hibernateBookRepository.remove(book2);
//        hibernateBookRepository.add(book2);
    }
    
    
}
