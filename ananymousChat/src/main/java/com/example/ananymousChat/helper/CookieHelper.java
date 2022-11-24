package com.example.ananymousChat.helper;

import javax.servlet.http.Cookie;

public class CookieHelper {

    public static <HttpServletRequest, Cookie> String extractCookie(HttpServletRequest request, String cookieName) {
        /*Cookie[] cookies = request.getCookies();
        if (cookies == null) return null;
        for (Cookie cookie : cookies) {
            if (cookie.getName().equals(cookieName))
                return cookie.getValue();
        }*/
        return null;
    }

    public static Cookie createCookie(String name, String value, String path) {
        Cookie cookie = new Cookie(name, value);
        cookie.setMaxAge(60 * 60 * 24 * 365);
        cookie.setPath(path);
        return cookie;
    }

}