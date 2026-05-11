package com.flashcard.analytics.application.handler;

import com.flashcard.analytics.domain.model.UserActivitySummary;
import com.flashcard.analytics.domain.repository.AnalyticsRepository;
import com.flashcard.shared.event.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class AnalyticsEventHandlerTest {

    private AnalyticsRepository repository;
    private AnalyticsEventHandler handler;
    private final LocalDateTime now = LocalDateTime.of(2026, 1, 1, 12, 0);

    @BeforeEach
    void setUp() {
        repository = mock(AnalyticsRepository.class);
        handler = new AnalyticsEventHandler(repository);
        when(repository.save(any())).thenAnswer(inv -> inv.getArgument(0));
    }

    @Test
    void onDeckCreated_createsNewSummaryIfNotExists() {
        when(repository.findByUserId(1L)).thenReturn(Optional.empty());

        handler.onDeckCreated(new DeckCreated(10L, "Title", "Desc", 1L, now));

        verify(repository).save(argThat(s ->
                s.getUserId().equals(1L) &&
                s.getTotalDecks() == 1 &&
                s.getTotalActions() == 1 &&
                s.getLastActivityAt().equals(now)
        ));
    }

    @Test
    void onDeckCreated_updatesExistingSummary() {
        UserActivitySummary existing = new UserActivitySummary(1L, 1L, 2, 5, 10, now.minusDays(1));
        when(repository.findByUserId(1L)).thenReturn(Optional.of(existing));

        handler.onDeckCreated(new DeckCreated(10L, "Title", "Desc", 1L, now));

        verify(repository).save(argThat(s ->
                s.getTotalDecks() == 3 &&
                s.getTotalActions() == 11
        ));
    }

    @Test
    void onDeckDeleted_decrementsDeckCount() {
        UserActivitySummary existing = new UserActivitySummary(1L, 1L, 2, 5, 10, now.minusDays(1));
        when(repository.findByUserId(1L)).thenReturn(Optional.of(existing));

        handler.onDeckDeleted(new DeckDeleted(10L, 1L, now));

        verify(repository).save(argThat(s ->
                s.getTotalDecks() == 1 &&
                s.getTotalActions() == 11
        ));
    }

    @Test
    void onCardAdded_incrementsCardCount() {
        when(repository.findByUserId(1L)).thenReturn(Optional.empty());

        handler.onCardAdded(new CardAdded(20L, 10L, "term", "def", 1L, now));

        verify(repository).save(argThat(s ->
                s.getTotalCards() == 1 &&
                s.getTotalActions() == 1
        ));
    }

    @Test
    void onCardDeleted_decrementsCardCount() {
        UserActivitySummary existing = new UserActivitySummary(1L, 1L, 1, 3, 5, now.minusDays(1));
        when(repository.findByUserId(1L)).thenReturn(Optional.of(existing));

        handler.onCardDeleted(new CardDeleted(20L, 10L, 1L, now));

        verify(repository).save(argThat(s ->
                s.getTotalCards() == 2 &&
                s.getTotalActions() == 6
        ));
    }

    @Test
    void onDeckUpdated_incrementsActionsOnly() {
        UserActivitySummary existing = new UserActivitySummary(1L, 1L, 2, 5, 10, now.minusDays(1));
        when(repository.findByUserId(1L)).thenReturn(Optional.of(existing));

        handler.onDeckUpdated(new DeckUpdated(10L, "New", "NewDesc", 1L, now));

        verify(repository).save(argThat(s ->
                s.getTotalDecks() == 2 &&
                s.getTotalCards() == 5 &&
                s.getTotalActions() == 11
        ));
    }

    @Test
    void onUserRegistered_incrementsActionsOnly() {
        when(repository.findByUserId(1L)).thenReturn(Optional.empty());

        handler.onUserRegistered(new UserRegistered(1L, "test@mail.com", "Test", now));

        verify(repository).save(argThat(s ->
                s.getUserId().equals(1L) &&
                s.getTotalActions() == 1
        ));
    }
}
