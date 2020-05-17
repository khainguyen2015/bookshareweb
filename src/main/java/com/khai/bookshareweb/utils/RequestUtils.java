/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.khai.bookshareweb.utils;

import javax.servlet.http.HttpServletRequest;

/**
 *
 * @author Acer
 */

public class RequestUtils {
    public static String getIpFromRequest(HttpServletRequest request) {
        String ipFromHeader = request.getHeader("X-FORWARDED-FOR");
        if(ipFromHeader != null && ipFromHeader.length() > 0) {
            return ipFromHeader.split(",")[0];
        }
        return request.getRemoteAddr();
    }
}
