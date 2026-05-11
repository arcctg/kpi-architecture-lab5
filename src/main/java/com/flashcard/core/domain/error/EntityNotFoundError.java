package com.flashcard.core.domain.error;

public class EntityNotFoundError extends DomainError {

    public EntityNotFoundError(String message) {
        super(message);
    }
}
