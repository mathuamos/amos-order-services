package com.jagaad.jagaadorderservices.utils;

import com.jagaad.jagaadorderservices.configs.ApplicationProperties;
import com.jagaad.jagaadorderservices.entities.Users;
import com.jagaad.jagaadorderservices.security.SecurityUtils;
import com.jagaad.jagaadorderservices.services.UserService;
import org.springframework.stereotype.Component;

import java.util.Calendar;
import java.util.Date;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Component
public class AppFunctions {

    private final ApplicationProperties applicationProperties;
    private final UserService userService;

    public AppFunctions(ApplicationProperties applicationProperties, UserService userService) {
        this.applicationProperties = applicationProperties;
        this.userService = userService;
    }

    //get orders update time checker
    public Date getSupportedOrderUpdateTime(){
        Date date ;
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(new Date());
        calendar.add(Calendar.MINUTE, Math.negateExact(applicationProperties.getUpdateOrderWithinMinutes()));
        date=calendar.getTime();
       return date;
    }



    //method to validate email
    public Boolean validateEmail(String email){
        try {
            Pattern VALID_EMAIL_ADDRESS_REGEX = Pattern.compile("^[A-Z0-9._%+-]+@[A-Z0-9.-]+\\.[A-Z]{2,6}$", Pattern.CASE_INSENSITIVE);
            Matcher matcher = VALID_EMAIL_ADDRESS_REGEX.matcher(email);
            return matcher.find();
        } catch (Exception ex) {

            return false;
        }
    }



    //method to get login user details
    public Users getLoginUser(){
        String username= SecurityUtils.getCurrentUserLogin();
       return   userService.findUserByUsername(username);
    }
}
