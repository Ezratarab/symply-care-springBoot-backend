package com.example.symply_care.exceptions;


public class TokenValidationException extends RuntimeException {
    public TokenValidationException(String string, Exception e) {
        super(string, e);
    }
}
