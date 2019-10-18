package com.platform.blog.config;

import java.util.Date;

public class SecurityConstants {

    public static final String SECRET = "haytham.dahri@gmail.com";
    public static final long EXPIRATION_TIME = 864_000_000; // 10 days
    public static final String TOKEN_PREFIX = "Bearer ";
    public static final String HEADER_STRING = "Authorization";
}
