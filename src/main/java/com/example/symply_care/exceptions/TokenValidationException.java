package com.example.symply_care.exceptions;

/**
 * This exception is thrown in case of a token validation error
 */
public class TokenValidationException extends RuntimeException {
    public TokenValidationException(String string, Exception e) {
        super(string, e);
    }
}
