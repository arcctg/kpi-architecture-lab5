package com.flashcard.core.application.card.query;

public record GetCardQuery(
        Long deckId,
        Long cardId,
        String userId
) {}
