package com.flashcard.core.application.card.query;

public record GetRandomCardQuery(
        Long deckId,
        String userId
) {}
