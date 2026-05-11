package com.flashcard.core.application.deck.query;

public record GetDeckQuery(
        Long deckId,
        String userId
) {}
