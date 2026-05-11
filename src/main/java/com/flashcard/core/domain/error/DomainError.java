package com.flashcard.core.domain.error;

public class DomainError extends RuntimeException {

    public DomainError(String message) {
        super(message);
    }
}
