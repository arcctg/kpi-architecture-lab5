package com.flashcard.core.application.card.command;

public record DeleteCardCommand(
        Long deckId,
        Long cardId,
        String userId
) {}
