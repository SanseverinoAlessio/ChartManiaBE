package com.chartmania.util;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.Cookie;

@Component
public class CookieUtil {
    @Value("${jwt.refresh.token.exp-minutes}")
    private long expMinutes;
    @Value("${secure.cookie}")
    private Boolean secureCookie;

    public void createRefreshCookie(HttpServletResponse response, String refreshToken) {
        Cookie cookie = new Cookie("refresh-token", refreshToken);
        cookie.setHttpOnly(true);
        cookie.setSecure(secureCookie != null ? secureCookie : false); 
        cookie.setPath("/");
        cookie.setMaxAge((int) expMinutes * 60);
        response.addCookie(cookie);
    }

    public void clearRefreshCookie(HttpServletResponse response) {
        Cookie cookie = new Cookie("refresh-token", null);
        cookie.setPath("/");
        cookie.setHttpOnly(true);
        cookie.setMaxAge(0); // This tells the browser to delete the cookie
        response.addCookie(cookie);
    }

    
}
