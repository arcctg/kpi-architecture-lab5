package com.flashcard.core.application.deck.query;

public record ListDecksQuery(
        String userId,
        int page,
        int size
) {}
