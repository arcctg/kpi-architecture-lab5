package com.flashcard.analytics.acl;

import com.flashcard.shared.event.*;

public class CoreEventTranslator {

    public static ActivityRecord fromUserRegistered(UserRegistered event) {
        return new ActivityRecord(
                event.userId(), ActivityType.USER_REGISTERED,
                "USER", event.userId(), event.occurredAt());
    }

    public static ActivityRecord fromDeckCreated(DeckCreated event) {
        return new ActivityRecord(
                event.ownerId(), ActivityType.DECK_CREATED,
                "DECK", event.deckId(), event.occurredAt());
    }

    public static ActivityRecord fromDeckUpdated(DeckUpdated event) {
        return new ActivityRecord(
                event.ownerId(), ActivityType.DECK_UPDATED,
                "DECK", event.deckId(), event.occurredAt());
    }

    public static ActivityRecord fromDeckDeleted(DeckDeleted event) {
        return new ActivityRecord(
                event.ownerId(), ActivityType.DECK_DELETED,
                "DECK", event.deckId(), event.occurredAt());
    }

    public static ActivityRecord fromCardAdded(CardAdded event) {
        return new ActivityRecord(
                event.ownerId(), ActivityType.CARD_ADDED,
                "CARD", event.cardId(), event.occurredAt());
    }

    public static ActivityRecord fromCardUpdated(CardUpdated event) {
        return new ActivityRecord(
                event.ownerId(), ActivityType.CARD_UPDATED,
                "CARD", event.cardId(), event.occurredAt());
    }

    public static ActivityRecord fromCardDeleted(CardDeleted event) {
        return new ActivityRecord(
                event.ownerId(), ActivityType.CARD_DELETED,
                "CARD", event.cardId(), event.occurredAt());
    }
}
