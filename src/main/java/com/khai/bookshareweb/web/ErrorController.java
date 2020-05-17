/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.khai.bookshareweb.web;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import static org.springframework.web.bind.annotation.RequestMethod.GET;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.server.ResponseStatusException;

/**
 *
 * @author Acer
 */

@Controller
@RequestMapping(value="/error/*")
public class ErrorController {
    
//    @RequestMapping(value="/",method=GET)
    public String loginFail(HttpServletRequest httpRequest) {
        int httpErrorCode = getErrorCode(httpRequest);
        switch (httpErrorCode) {
            case 400: {

                break;
            }
            case 401: {
     
                break;
            }
            case 404: {
                return "errors/404";
            }
            case 500: {
            
                break;
            }     
        }
        return "index";
    }
    
    private int getErrorCode(HttpServletRequest httpRequest) {
        return (Integer) httpRequest
          .getAttribute("javax.servlet.error.status_code");
    }
    
    @RequestMapping(value = "/404", method = GET)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public String error404() {
        System.out.println("Exception");
//        throw new RuntimeException("Error");
        return "errors/404";
    }
    
    @RequestMapping(value = "*")
    public String errors() {
        return "errors/genericError";
        
    }
}
