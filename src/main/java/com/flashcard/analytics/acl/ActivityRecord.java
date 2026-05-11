package com.flashcard.analytics.acl;

import java.time.LocalDateTime;

public record ActivityRecord(
        Long userId,
        ActivityType type,
        String entityType,
        Long entityId,
        LocalDateTime occurredAt
) {}
