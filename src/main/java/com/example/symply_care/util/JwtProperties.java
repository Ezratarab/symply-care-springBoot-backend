package com.example.symply_care.util;


public class JwtProperties {
    public static final int EXPIRATION_TIME = 4 * 60 * 60 * 1000; // 4 שעות

    public static final String TOKEN_PREFIX = "Bearer ";

    public static final String HEADER_STRING = "Authorization";

    public static final int IDLE_TIME_FOR_REFRESH_TOKEN = 45 * 60 * 1000; //45 דקות
    public static final int EXPIRATION_TIME_FOR_REFRESH_TOKEN = EXPIRATION_TIME + IDLE_TIME_FOR_REFRESH_TOKEN;

}
