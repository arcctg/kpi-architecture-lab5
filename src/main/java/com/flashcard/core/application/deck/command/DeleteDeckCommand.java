package com.flashcard.core.application.deck.command;

public record DeleteDeckCommand(
        Long deckId,
        String userId
) {}
