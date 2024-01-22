package com.example.symply_care.util;


public class JwtProperties {
    // The EXPIRATION_TIME constant is used to set the expiration time of the JWT
    public static final int EXPIRATION_TIME = 6 * 60 * 60 * 1000; // 6 hours

    // The TOKEN_PREFIX constant is used to prefix the JWT in the Authorization header
    public static final String TOKEN_PREFIX = "Bearer ";

    // The HEADER_STRING constant is used to set the key of the Authorization header
    public static final String HEADER_STRING = "Authorization";

    public static final int IDLE_TIME_FOR_REFRESH_TOKEN = 30 * 60 * 1000; // 30 minutes
    // 20 minutes
    public static final int EXPIRATION_TIME_FOR_REFRESH_TOKEN = EXPIRATION_TIME + IDLE_TIME_FOR_REFRESH_TOKEN;

    /*
    EXPIRATION_TIME: This constant defines the duration for which a JWT remains valid after it is issued.
    In hour case, it's set to 6 hours (6 hours * 60 minutes/hour * 60 seconds/minute * 1000 milliseconds/second).
    After this period, the JWT is considered expired.

    TOKEN_PREFIX: This is a string constant used to prefix the JWT in the HTTP Authorization header.
    The common convention for JWTs is to prefix them with "Bearer ", indicating that the following token is a bearer token.

    HEADER_STRING: This constant defines the key name used in the HTTP header for passing the JWT.
    Typically, JWTs are included in the Authorization header of HTTP requests.

    IDLE_TIME_FOR_REFRESH_TOKEN: This constant specifies a grace period or leeway time in addition to the standard expiration time.
    In this code, it's set to 30 minutes.
    This period is often used to allow slightly expired tokens to be refreshed or replaced, providing a smoother user
     experience.

    EXPIRATION_TIME_FOR_REFRESH_TOKEN: This constant combines the standard expiration time of the JWT with the additional grace period.
    The total time calculated is the sum of the EXPIRATION_TIME and the IDLE_TIME_FOR_REFRESH_TOKEN.
    In this case, this would be the regular 6-hour expiration plus an additional 30-minute grace period.

    The use of these constants helps to standardize JWT handling across your application,
    making the code more maintainable and the behavior of JWT processing consistent.
    It's important to note that the values for these constants, especially the SECRET and expiration times,
     should be carefully chosen based on the security requirements and usage patterns of this application.






     */

}
