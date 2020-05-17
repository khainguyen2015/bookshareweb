/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.khai.bookshareweb.common;

import java.util.Calendar;
import java.util.Date;

/**
 *
 * @author Acer
 */
public class DateUtils {
    
    
    
    public static String getMeaningfulDate(Date date) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        int postYear = cal.get(Calendar.YEAR);
        int postMonth = cal.get(Calendar.MONTH);
        int postDay = cal.get(Calendar.DAY_OF_MONTH);
        int postHour = cal.get(Calendar.HOUR_OF_DAY);
        int postMinute = cal.get(Calendar.MINUTE);
        int postSecond = cal.get(Calendar.SECOND);
        
        cal.setTime(new Date());
        int currentYear = cal.get(Calendar.YEAR);
        int currentMonth = cal.get(Calendar.MONTH);
        int currentDay = cal.get(Calendar.DAY_OF_MONTH);
        int currentHour = cal.get(Calendar.HOUR_OF_DAY);
        int currentMinute = cal.get(Calendar.MINUTE);
        int currentSecond = cal.get(Calendar.SECOND);
        
        

        if(currentYear - postYear > 0) {
            return  (currentYear - postYear) + " năm trước";
        }
        
        if(currentMonth - postMonth > 0) {
            return  (currentMonth - postMonth) + " tháng trước";
        }
        
        if(currentDay - postDay > 0) {
            int numOfDay = currentDay - postDay;
            if(numOfDay > 7) {
                if(numOfDay / 7 > 1) {
                    return  (numOfDay / 7) + " tuần trước";
                }
            }
            return  (currentDay - postDay) + " ngày trước";
        }
        
        if(currentHour - postHour > 0) {
            return  (currentHour - postHour) + " giờ trước";
        }
        
        if(currentMinute - postMinute > 0) {
            return  (currentMinute - postMinute) + " phút trước";
        }
        
        if(currentSecond - postSecond > 0) {
            return  (currentSecond - postSecond) + " giây trước";
        } 
        
        return "";
    }

}
