package com.flashcard.shared.event;

import java.time.LocalDateTime;

public record DeckCreated(
        Long deckId,
        String title,
        String description,
        Long ownerId,
        LocalDateTime occurredAt
) {}
