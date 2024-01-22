package com.example.symply_care.exceptions;


/**
 * This exception is thrown in case of an authentication error
 */

// This exception is thrown in case of an authentication error
public class AuthenticationServiceException extends RuntimeException {
    public AuthenticationServiceException(String message) {
        super(message);
    }
}
