package com.flashcard.analytics.infrastructure.persistence;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "user_activity_summary")
public class UserActivitySummaryEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "user_id", nullable = false, unique = true)
    private Long userId;

    @Column(name = "total_decks", nullable = false)
    private int totalDecks;

    @Column(name = "total_cards", nullable = false)
    private int totalCards;

    @Column(name = "total_actions", nullable = false)
    private int totalActions;

    @Column(name = "last_activity_at")
    private LocalDateTime lastActivityAt;

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public Long getUserId() { return userId; }
    public void setUserId(Long userId) { this.userId = userId; }
    public int getTotalDecks() { return totalDecks; }
    public void setTotalDecks(int totalDecks) { this.totalDecks = totalDecks; }
    public int getTotalCards() { return totalCards; }
    public void setTotalCards(int totalCards) { this.totalCards = totalCards; }
    public int getTotalActions() { return totalActions; }
    public void setTotalActions(int totalActions) { this.totalActions = totalActions; }
    public LocalDateTime getLastActivityAt() { return lastActivityAt; }
    public void setLastActivityAt(LocalDateTime lastActivityAt) { this.lastActivityAt = lastActivityAt; }
}
