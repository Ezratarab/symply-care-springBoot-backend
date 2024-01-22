package com.example.symply_care.exceptions;


/** This exception is thrown in case of a token refresh error
 *
 */
public class TokenRefreshException extends RuntimeException {
    public TokenRefreshException(String message) {
        super(message);
    }

}
