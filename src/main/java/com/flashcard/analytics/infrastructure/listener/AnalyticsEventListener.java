package com.flashcard.analytics.infrastructure.listener;

import com.flashcard.analytics.application.handler.AnalyticsEventHandler;
import com.flashcard.shared.event.*;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

@Component
public class AnalyticsEventListener {

    private final AnalyticsEventHandler handler;

    public AnalyticsEventListener(AnalyticsEventHandler handler) {
        this.handler = handler;
    }

    @Async("eventExecutor")
    @EventListener
    public void onUserRegistered(UserRegistered event) {
        handler.onUserRegistered(event);
    }

    @Async("eventExecutor")
    @EventListener
    public void onDeckCreated(DeckCreated event) {
        handler.onDeckCreated(event);
    }

    @Async("eventExecutor")
    @EventListener
    public void onDeckUpdated(DeckUpdated event) {
        handler.onDeckUpdated(event);
    }

    @Async("eventExecutor")
    @EventListener
    public void onDeckDeleted(DeckDeleted event) {
        handler.onDeckDeleted(event);
    }

    @Async("eventExecutor")
    @EventListener
    public void onCardAdded(CardAdded event) {
        handler.onCardAdded(event);
    }

    @Async("eventExecutor")
    @EventListener
    public void onCardUpdated(CardUpdated event) {
        handler.onCardUpdated(event);
    }

    @Async("eventExecutor")
    @EventListener
    public void onCardDeleted(CardDeleted event) {
        handler.onCardDeleted(event);
    }
}
