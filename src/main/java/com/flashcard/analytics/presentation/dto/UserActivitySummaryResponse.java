package com.flashcard.analytics.presentation.dto;

import java.time.LocalDateTime;

public record UserActivitySummaryResponse(
        Long userId,
        int totalDecks,
        int totalCards,
        int totalActions,
        LocalDateTime lastActivityAt
) {}
