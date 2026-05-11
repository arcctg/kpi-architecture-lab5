package com.flashcard.analytics.integration;

import com.flashcard.analytics.domain.repository.AnalyticsRepository;
import com.flashcard.core.application.port.EventPublisher;
import com.flashcard.shared.event.CardAdded;
import com.flashcard.shared.event.DeckCreated;
import com.flashcard.shared.event.DeckDeleted;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
class AnalyticsIT {

    @Autowired
    private EventPublisher eventPublisher;

    @Autowired
    private AnalyticsRepository analyticsRepository;

    @Test
    void eventDrivenAnalytics_projectsCorrectSummary() throws InterruptedException {
        Long userId = 999L;
        LocalDateTime now = LocalDateTime.now();

        eventPublisher.publish(new DeckCreated(100L, "Deck1", "Desc", userId, now));
        eventPublisher.publish(new DeckCreated(101L, "Deck2", "Desc", userId, now));
        eventPublisher.publish(new CardAdded(200L, 100L, "term1", "def1", userId, now));
        eventPublisher.publish(new CardAdded(201L, 100L, "term2", "def2", userId, now));
        eventPublisher.publish(new CardAdded(202L, 101L, "term3", "def3", userId, now));
        eventPublisher.publish(new DeckDeleted(101L, userId, now));

        Thread.sleep(2000);

        var summary = analyticsRepository.findByUserId(userId);
        assertTrue(summary.isPresent());
        assertEquals(1, summary.get().getTotalDecks());
        assertEquals(3, summary.get().getTotalCards());
        assertEquals(6, summary.get().getTotalActions());
        assertNotNull(summary.get().getLastActivityAt());
    }
}
