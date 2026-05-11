package com.flashcard.shared.event;

import java.time.LocalDateTime;

public record DeckDeleted(
        Long deckId,
        Long ownerId,
        LocalDateTime occurredAt
) {}
