package com.flashcard.shared.event;

import java.time.LocalDateTime;

public record CardDeleted(
        Long cardId,
        Long deckId,
        Long ownerId,
        LocalDateTime occurredAt
) {}
