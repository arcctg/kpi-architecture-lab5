package com.flashcard.analytics.application.handler;

import com.flashcard.analytics.acl.ActivityRecord;
import com.flashcard.analytics.acl.CoreEventTranslator;
import com.flashcard.analytics.domain.model.UserActivitySummary;
import com.flashcard.analytics.domain.repository.AnalyticsRepository;

public class AnalyticsEventHandler {

    private final AnalyticsRepository analyticsRepository;

    public AnalyticsEventHandler(AnalyticsRepository analyticsRepository) {
        this.analyticsRepository = analyticsRepository;
    }

    public void onUserRegistered(com.flashcard.shared.event.UserRegistered event) {
        ActivityRecord record = CoreEventTranslator.fromUserRegistered(event);
        UserActivitySummary summary = getOrCreate(record.userId());
        summary.recordAction(record.occurredAt());
        analyticsRepository.save(summary);
    }

    public void onDeckCreated(com.flashcard.shared.event.DeckCreated event) {
        ActivityRecord record = CoreEventTranslator.fromDeckCreated(event);
        UserActivitySummary summary = getOrCreate(record.userId());
        summary.recordDeckCreated(record.occurredAt());
        analyticsRepository.save(summary);
    }

    public void onDeckUpdated(com.flashcard.shared.event.DeckUpdated event) {
        ActivityRecord record = CoreEventTranslator.fromDeckUpdated(event);
        UserActivitySummary summary = getOrCreate(record.userId());
        summary.recordAction(record.occurredAt());
        analyticsRepository.save(summary);
    }

    public void onDeckDeleted(com.flashcard.shared.event.DeckDeleted event) {
        ActivityRecord record = CoreEventTranslator.fromDeckDeleted(event);
        UserActivitySummary summary = getOrCreate(record.userId());
        summary.recordDeckDeleted(record.occurredAt());
        analyticsRepository.save(summary);
    }

    public void onCardAdded(com.flashcard.shared.event.CardAdded event) {
        ActivityRecord record = CoreEventTranslator.fromCardAdded(event);
        UserActivitySummary summary = getOrCreate(record.userId());
        summary.recordCardAdded(record.occurredAt());
        analyticsRepository.save(summary);
    }

    public void onCardUpdated(com.flashcard.shared.event.CardUpdated event) {
        ActivityRecord record = CoreEventTranslator.fromCardUpdated(event);
        UserActivitySummary summary = getOrCreate(record.userId());
        summary.recordAction(record.occurredAt());
        analyticsRepository.save(summary);
    }

    public void onCardDeleted(com.flashcard.shared.event.CardDeleted event) {
        ActivityRecord record = CoreEventTranslator.fromCardDeleted(event);
        UserActivitySummary summary = getOrCreate(record.userId());
        summary.recordCardDeleted(record.occurredAt());
        analyticsRepository.save(summary);
    }

    private UserActivitySummary getOrCreate(Long userId) {
        return analyticsRepository.findByUserId(userId)
                .orElseGet(() -> UserActivitySummary.createEmpty(userId));
    }
}
