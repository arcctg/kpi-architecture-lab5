package com.flashcard.analytics.infrastructure.persistence;

import com.flashcard.analytics.domain.model.UserActivitySummary;
import com.flashcard.analytics.domain.repository.AnalyticsRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public class AnalyticsRepositoryAdapter implements AnalyticsRepository {

    private final JpaAnalyticsRepository jpa;

    public AnalyticsRepositoryAdapter(JpaAnalyticsRepository jpa) {
        this.jpa = jpa;
    }

    @Override
    public Optional<UserActivitySummary> findByUserId(Long userId) {
        return jpa.findByUserId(userId).map(this::toDomain);
    }

    @Override
    public UserActivitySummary save(UserActivitySummary summary) {
        UserActivitySummaryEntity entity = toEntity(summary);
        UserActivitySummaryEntity saved = jpa.save(entity);
        return toDomain(saved);
    }

    private UserActivitySummary toDomain(UserActivitySummaryEntity entity) {
        return new UserActivitySummary(
                entity.getId(), entity.getUserId(),
                entity.getTotalDecks(), entity.getTotalCards(),
                entity.getTotalActions(), entity.getLastActivityAt());
    }

    private UserActivitySummaryEntity toEntity(UserActivitySummary summary) {
        UserActivitySummaryEntity entity = new UserActivitySummaryEntity();
        entity.setId(summary.getId());
        entity.setUserId(summary.getUserId());
        entity.setTotalDecks(summary.getTotalDecks());
        entity.setTotalCards(summary.getTotalCards());
        entity.setTotalActions(summary.getTotalActions());
        entity.setLastActivityAt(summary.getLastActivityAt());
        return entity;
    }
}
