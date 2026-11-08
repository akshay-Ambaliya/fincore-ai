package com.fincore.identity.exceptions;

public class InvalidRefreshTokenException extends RuntimeException {

        public InvalidRefreshTokenException(String message) {
            super(message);
        }
    }