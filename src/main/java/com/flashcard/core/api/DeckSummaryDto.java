package com.flashcard.core.api;

import java.time.LocalDateTime;

public record DeckSummaryDto(
        Long id,
        String title,
        String description,
        int cardCount,
        LocalDateTime createdAt
) {}
