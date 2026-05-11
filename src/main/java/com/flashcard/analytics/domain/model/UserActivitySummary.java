package com.flashcard.analytics.domain.model;

import java.time.LocalDateTime;

public class UserActivitySummary {

    private Long id;
    private final Long userId;
    private int totalDecks;
    private int totalCards;
    private int totalActions;
    private LocalDateTime lastActivityAt;

    public UserActivitySummary(Long id, Long userId, int totalDecks, int totalCards,
                               int totalActions, LocalDateTime lastActivityAt) {
        this.id = id;
        this.userId = userId;
        this.totalDecks = totalDecks;
        this.totalCards = totalCards;
        this.totalActions = totalActions;
        this.lastActivityAt = lastActivityAt;
    }

    public static UserActivitySummary createEmpty(Long userId) {
        return new UserActivitySummary(null, userId, 0, 0, 0, null);
    }

    public void recordDeckCreated(LocalDateTime at) {
        this.totalDecks++;
        this.totalActions++;
        this.lastActivityAt = at;
    }

    public void recordDeckDeleted(LocalDateTime at) {
        this.totalDecks = Math.max(0, this.totalDecks - 1);
        this.totalActions++;
        this.lastActivityAt = at;
    }

    public void recordCardAdded(LocalDateTime at) {
        this.totalCards++;
        this.totalActions++;
        this.lastActivityAt = at;
    }

    public void recordCardDeleted(LocalDateTime at) {
        this.totalCards = Math.max(0, this.totalCards - 1);
        this.totalActions++;
        this.lastActivityAt = at;
    }

    public void recordAction(LocalDateTime at) {
        this.totalActions++;
        this.lastActivityAt = at;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public Long getUserId() { return userId; }
    public int getTotalDecks() { return totalDecks; }
    public int getTotalCards() { return totalCards; }
    public int getTotalActions() { return totalActions; }
    public LocalDateTime getLastActivityAt() { return lastActivityAt; }
}
