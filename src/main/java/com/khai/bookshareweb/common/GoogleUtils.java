/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.khai.bookshareweb.common;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.api.client.googleapis.auth.oauth2.GoogleAuthorizationCodeTokenRequest;
import com.google.api.client.googleapis.auth.oauth2.GoogleTokenResponse;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.khai.bookshareweb.dto.UserDto;
import com.khai.bookshareweb.entity.UserAccount;
import com.khai.bookshareweb.service.UserService;
import com.khai.bookshareweb.service.exception.AccountExistsException;
import com.khai.bookshareweb.service.exception.EmailExistsException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.fluent.Form;
import org.apache.http.client.fluent.Request;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

/**
 *
 * @author Acer
 */

@Component
public class GoogleUtils {
    
    @Autowired
    private Environment env;

    @Autowired
    private UserService userService;
    
    public String getToken(final String code) throws ClientProtocolException, IOException {
        String link = env.getProperty("google.link.get.token");
        
//        GoogleTokenResponse tokenResponse =
//            new GoogleAuthorizationCodeTokenRequest(
//                new NetHttpTransport(),
//                JacksonFactory.getDefaultInstance(),
//                "https://oauth2.googleapis.com/token",
//                env.getProperty("google.app.id"),
//                env.getProperty("google.app.secret"),
//                code,
//                "http://localhost:8080"
//            ).execute();
//            
//
//        String accessToken = tokenResponse.getAccessToken();
//        System.out.println(accessToken);
//        return null;
        
        String response = Request.Post(link)
                .bodyForm(Form.form().add("client_id", env.getProperty("google.app.id"))
                .add("client_secret", env.getProperty("google.app.secret"))
                .add("redirect_uri", env.getProperty("google.redirect.uri"))
                .add("code", code)
                .add("grant_type", "authorization_code").build()
                ).execute().returnContent().asString();
        System.out.println(response);
        ObjectMapper mapper = new ObjectMapper();
        JsonNode root = mapper.readTree(response);
        JsonNode idTokenNode = root.get("id_token");
        if(!verifyIDToken(idTokenNode.textValue())) {
            return null;
        }
        JsonNode accessTokenNode = root.get("access_token");
        return accessTokenNode.textValue();
    }
    
    public GooglePojo getUserInfo(final String accessToken) throws ClientProtocolException, IOException {
        String link = env.getProperty("google.link.get.user_info") + accessToken;
        String response = Request.Get(link).execute().returnContent().asString();
        System.out.println(response);
        
        
        ObjectMapper mapper = new ObjectMapper();
        GooglePojo googlePojo = mapper.readValue(response, GooglePojo.class);
        System.out.println(googlePojo.getEmail());
        System.out.println(googlePojo.getPicture());
        return googlePojo;
    }
    
    public UserDetails buildUser(GooglePojo googlePojo) {
        boolean enable = true;
        boolean accountNonExpired = true;
        boolean credentialsNonExpired = true;
        boolean accountNonLocked = true;
        List<GrantedAuthority> authorities = new ArrayList<>();
        authorities.add(new SimpleGrantedAuthority("ROLE_USER"));
        
        //Create random password for new account login using google
        String password = UUID.randomUUID().toString();
        
        UserDetails userDetail = new User(googlePojo.getEmail(),
            password, enable, accountNonExpired, credentialsNonExpired, accountNonLocked, authorities);
        return userDetail;
    }
    
    public UserAccount createNewUser(GooglePojo googlePojo) throws EmailExistsException, AccountExistsException {
        //Create random password for new account login using google
        String password = UUID.randomUUID().toString();
        
        UserDto userDto = new UserDto();
        userDto.setAccountName(googlePojo.getEmail());
        userDto.setEmail(googlePojo.getEmail());
        userDto.setUserName(googlePojo.getName());
        userDto.setPassword(password);
        userDto.setPicture(googlePojo.getPicture());
        UserAccount userAccount= userService.registerNewUserAccount(userDto);
        return userAccount;
    }
    
    private boolean verifyIDToken(String idToken) throws ClientProtocolException, IOException {
        String link = env.getProperty("google.link.get.validate_token_info") + idToken;
        String response = Request.Get(link).execute().returnContent().asString();
        System.out.println(response);
        ObjectMapper mapper = new ObjectMapper();
        JsonNode iss = mapper.readTree(response).get("aud");
        
        return iss != null ? iss.textValue().equals(env.getProperty("google.app.id")) : false;
    }
}
