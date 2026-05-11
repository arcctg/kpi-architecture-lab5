package com.flashcard.core.application.deck.command;

public record CreateDeckCommand(
        String title,
        String description,
        String userId
) {}
