package com.flashcard.analytics.domain.repository;

import com.flashcard.analytics.domain.model.UserActivitySummary;

import java.util.Optional;

public interface AnalyticsRepository {

    Optional<UserActivitySummary> findByUserId(Long userId);

    UserActivitySummary save(UserActivitySummary summary);
}
