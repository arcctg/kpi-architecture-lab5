package com.flashcard.analytics.api;

import java.time.LocalDateTime;

public record UserActivitySummaryDto(
        Long userId,
        int totalDecks,
        int totalCards,
        int totalActions,
        LocalDateTime lastActivityAt
) {}
