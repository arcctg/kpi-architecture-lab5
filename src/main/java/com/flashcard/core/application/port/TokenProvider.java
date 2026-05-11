package com.flashcard.core.application.port;

public interface TokenProvider {

    String generateToken(String subject);
}
