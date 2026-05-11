package com.flashcard.analytics.acl;

import com.flashcard.shared.event.*;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

class CoreEventTranslatorTest {

    private final LocalDateTime now = LocalDateTime.of(2026, 1, 1, 12, 0);

    @Test
    void fromUserRegistered_translatesCorrectly() {
        UserRegistered event = new UserRegistered(1L, "test@mail.com", "Test", now);
        ActivityRecord record = CoreEventTranslator.fromUserRegistered(event);
        assertEquals(1L, record.userId());
        assertEquals(ActivityType.USER_REGISTERED, record.type());
        assertEquals("USER", record.entityType());
        assertEquals(1L, record.entityId());
        assertEquals(now, record.occurredAt());
    }

    @Test
    void fromDeckCreated_translatesCorrectly() {
        DeckCreated event = new DeckCreated(10L, "Title", "Desc", 1L, now);
        ActivityRecord record = CoreEventTranslator.fromDeckCreated(event);
        assertEquals(1L, record.userId());
        assertEquals(ActivityType.DECK_CREATED, record.type());
        assertEquals("DECK", record.entityType());
        assertEquals(10L, record.entityId());
    }

    @Test
    void fromDeckUpdated_translatesCorrectly() {
        DeckUpdated event = new DeckUpdated(10L, "New", "NewDesc", 1L, now);
        ActivityRecord record = CoreEventTranslator.fromDeckUpdated(event);
        assertEquals(1L, record.userId());
        assertEquals(ActivityType.DECK_UPDATED, record.type());
    }

    @Test
    void fromDeckDeleted_translatesCorrectly() {
        DeckDeleted event = new DeckDeleted(10L, 1L, now);
        ActivityRecord record = CoreEventTranslator.fromDeckDeleted(event);
        assertEquals(ActivityType.DECK_DELETED, record.type());
        assertEquals(10L, record.entityId());
    }

    @Test
    void fromCardAdded_translatesCorrectly() {
        CardAdded event = new CardAdded(20L, 10L, "term", "def", 1L, now);
        ActivityRecord record = CoreEventTranslator.fromCardAdded(event);
        assertEquals(1L, record.userId());
        assertEquals(ActivityType.CARD_ADDED, record.type());
        assertEquals("CARD", record.entityType());
        assertEquals(20L, record.entityId());
    }

    @Test
    void fromCardUpdated_translatesCorrectly() {
        CardUpdated event = new CardUpdated(20L, 10L, "new", "newdef", 1L, now);
        ActivityRecord record = CoreEventTranslator.fromCardUpdated(event);
        assertEquals(ActivityType.CARD_UPDATED, record.type());
    }

    @Test
    void fromCardDeleted_translatesCorrectly() {
        CardDeleted event = new CardDeleted(20L, 10L, 1L, now);
        ActivityRecord record = CoreEventTranslator.fromCardDeleted(event);
        assertEquals(ActivityType.CARD_DELETED, record.type());
        assertEquals(20L, record.entityId());
    }
}
