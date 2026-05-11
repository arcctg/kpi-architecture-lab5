package com.flashcard.analytics.infrastructure.persistence;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface JpaAnalyticsRepository extends JpaRepository<UserActivitySummaryEntity, Long> {

    Optional<UserActivitySummaryEntity> findByUserId(Long userId);
}

