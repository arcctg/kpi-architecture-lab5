package com.flashcard.core.application.card.query;

public record ListCardsQuery(
        Long deckId,
        String userId,
        String search,
        int page,
        int size
) {}
