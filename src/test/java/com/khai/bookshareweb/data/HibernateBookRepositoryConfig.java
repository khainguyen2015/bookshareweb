/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.khai.bookshareweb.data;

import java.util.Properties;
import javax.persistence.EntityManagerFactory;
import javax.sql.DataSource;
import org.apache.commons.dbcp2.BasicDataSource;
import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.context.support.ReloadableResourceBundleMessageSource;
import org.springframework.context.support.ResourceBundleMessageSource;
import org.springframework.core.env.Environment;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.datasource.embedded.EmbeddedDatabaseBuilder;
import org.springframework.jdbc.datasource.embedded.EmbeddedDatabaseType;
import org.springframework.jndi.JndiObjectFactoryBean;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.springframework.orm.hibernate5.HibernateTransactionManager;
import org.springframework.orm.hibernate5.LocalSessionFactoryBean;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.orm.jpa.JpaVendorAdapter;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.orm.jpa.vendor.Database;
import org.springframework.orm.jpa.vendor.HibernateJpaVendorAdapter;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.annotation.EnableTransactionManagement;


/**
 *
 * @author Acer
 */

@Configuration
@PropertySource("classpath:application.properties")
@ComponentScan(value = {"com.khai.bookshareweb.data"})
@EnableTransactionManagement
public class HibernateBookRepositoryConfig {
    
    @Autowired
    private Environment env;
    
    
    //@Bean
    //@Profile("product")
    public JndiObjectFactoryBean dataSource() {
        JndiObjectFactoryBean jndiObjectFb = new JndiObjectFactoryBean();
        jndiObjectFb.setJndiName("jdbc/MySqlDS");
        jndiObjectFb.setResourceRef(true);
        jndiObjectFb.setProxyInterface(DataSource.class);
        return jndiObjectFb;
    }
    
    @Bean
    public BasicDataSource basicDataSource() {
        BasicDataSource dataSource = new BasicDataSource();
        dataSource.setDriverClassName(env.getProperty("spring.datasource.driverClassName"));
        dataSource.setUrl(env.getProperty("spring.datasource.jdbcUrl"));
        dataSource.setUsername(env.getProperty("spring.datasource.username"));
        dataSource.setPassword(env.getProperty("spring.datasource.password"));
        dataSource.setInitialSize(Integer.parseInt(env.getProperty("spring.datasource.initialSize")));
        dataSource.setMaxTotal(Integer.parseInt(env.getProperty("spring.datasource.maxTotal")));
        return dataSource;
    }
    
    
//    @Bean
//    @Profile("dev")
    public DataSource dataSoure() {
        return new EmbeddedDatabaseBuilder()
                .setType(EmbeddedDatabaseType.H2)
                .addScript("")
                .addScript("")
                .build();
    }
    
    //@Bean
    public LocalContainerEntityManagerFactoryBean entityManagerFactory(DataSource dataSource, JpaVendorAdapter jpaVendorAdapter) {
        LocalContainerEntityManagerFactoryBean emfb = new LocalContainerEntityManagerFactoryBean();
        emfb.setDataSource(dataSource);
        emfb.setJpaVendorAdapter(jpaVendorAdapter);
        emfb.setPackagesToScan("com.khai.bookshareweb.entity");
        return emfb;
    }
    
    //@Bean
    public JpaVendorAdapter jpaVendorAdapter() {
        HibernateJpaVendorAdapter adapter = new HibernateJpaVendorAdapter();
        adapter.setDatabase(Database.MYSQL);
        adapter.setShowSql(true);
        adapter.setDatabasePlatform("org.hibernate.dialect.MySQLDialect");
        return adapter;
    }
    
//    @Bean
    public JdbcTemplate jdbcTemplate(DataSource dataSource) {
        return new JdbcTemplate(dataSource);
    }
    
    //@Bean
    public PlatformTransactionManager transactionManager(EntityManagerFactory emf) {
        return new JpaTransactionManager(emf);
    }
    
    @Bean
    public MessageSource messageSource() {
        ReloadableResourceBundleMessageSource  messageSource = new ReloadableResourceBundleMessageSource();
        messageSource.setBasename("classpath:messages");
        messageSource.setDefaultEncoding("UTF-8");
        return messageSource;
    }
    
    @Bean
    public LocalSessionFactoryBean sessionFactory(DataSource dataSource) {
        LocalSessionFactoryBean sfb = new LocalSessionFactoryBean();
        sfb.setDataSource(dataSource);
        sfb.setPackagesToScan("com.khai.bookshareweb.entity");
        Properties props = new Properties();
        props.setProperty("dialect", "org.hibernate.dialect.MySQL8Dialect");
        sfb.setHibernateProperties(props);
        return sfb;
    }
    
    
    @Bean
    public HibernateTransactionManager transactionManager(SessionFactory sessionFactory){
        return new HibernateTransactionManager(sessionFactory);
    }
    
    @Bean
    public JavaMailSender getJavaMailSender() {
        JavaMailSenderImpl mailSender = new JavaMailSenderImpl();
        mailSender.setHost(env.getProperty("spring.mail.host"));
        System.out.println(env.getProperty("spring.mail.host"));
        System.out.println(env.getProperty("spring.mail.port"));
        mailSender.setPort(Integer.parseInt(env.getProperty("spring.mail.port")));

        mailSender.setUsername(env.getProperty("spring.mail.username"));
        mailSender.setPassword(env.getProperty("spring.mail.password"));

        Properties props = mailSender.getJavaMailProperties();
        props.put("mail.transport.protocol", env.getProperty("spring.mail.properties.mail.transport.protocol"));
        props.put("mail.smtp.auth", env.getProperty("spring.mail.properties.mail.smtp.auth"));
        props.put("mail.smtp.starttls.enable", env.getProperty("spring.mail.properties.mail.smtp.starttls.enable"));
        props.put("mail.debug", env.getProperty("spring.mail.properties.mail.debug"));
        props.put("mail.smtp.ssl.trust", env.getProperty("spring.mail.properties.mail.smtp.ssl.trust"));

        return mailSender;
    }
}
