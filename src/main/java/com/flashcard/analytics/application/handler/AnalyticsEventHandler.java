package com.flashcard.analytics.application.handler;

import com.flashcard.analytics.acl.ActivityRecord;
import com.flashcard.analytics.acl.CoreEventTranslator;
import com.flashcard.analytics.domain.model.UserActivitySummary;
import com.flashcard.analytics.domain.repository.AnalyticsRepository;
import com.flashcard.shared.event.*;

public class AnalyticsEventHandler {

    private final AnalyticsRepository analyticsRepository;

    public AnalyticsEventHandler(AnalyticsRepository analyticsRepository) {
        this.analyticsRepository = analyticsRepository;
    }

    public void onUserRegistered(UserRegistered event) {
        ActivityRecord record = CoreEventTranslator.fromUserRegistered(event);
        UserActivitySummary summary = getOrCreate(record.userId());
        summary.recordAction(record.occurredAt());
        analyticsRepository.save(summary);
    }

    public void onDeckCreated(DeckCreated event) {
        ActivityRecord record = CoreEventTranslator.fromDeckCreated(event);
        UserActivitySummary summary = getOrCreate(record.userId());
        summary.recordDeckCreated(record.occurredAt());
        analyticsRepository.save(summary);
    }

    public void onDeckUpdated(DeckUpdated event) {
        ActivityRecord record = CoreEventTranslator.fromDeckUpdated(event);
        UserActivitySummary summary = getOrCreate(record.userId());
        summary.recordAction(record.occurredAt());
        analyticsRepository.save(summary);
    }

    public void onDeckDeleted(DeckDeleted event) {
        ActivityRecord record = CoreEventTranslator.fromDeckDeleted(event);
        UserActivitySummary summary = getOrCreate(record.userId());
        summary.recordDeckDeleted(record.occurredAt());
        analyticsRepository.save(summary);
    }

    public void onCardAdded(CardAdded event) {
        ActivityRecord record = CoreEventTranslator.fromCardAdded(event);
        UserActivitySummary summary = getOrCreate(record.userId());
        summary.recordCardAdded(record.occurredAt());
        analyticsRepository.save(summary);
    }

    public void onCardUpdated(CardUpdated event) {
        ActivityRecord record = CoreEventTranslator.fromCardUpdated(event);
        UserActivitySummary summary = getOrCreate(record.userId());
        summary.recordAction(record.occurredAt());
        analyticsRepository.save(summary);
    }

    public void onCardDeleted(CardDeleted event) {
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
