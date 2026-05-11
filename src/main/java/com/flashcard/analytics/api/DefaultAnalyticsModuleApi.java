package com.flashcard.analytics.api;

import com.flashcard.analytics.domain.model.UserActivitySummary;
import com.flashcard.analytics.domain.repository.AnalyticsRepository;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class DefaultAnalyticsModuleApi implements AnalyticsModuleApi {

    private final AnalyticsRepository analyticsRepository;

    public DefaultAnalyticsModuleApi(AnalyticsRepository analyticsRepository) {
        this.analyticsRepository = analyticsRepository;
    }

    @Override
    public Optional<UserActivitySummaryDto> getUserSummary(Long userId) {
        return analyticsRepository.findByUserId(userId)
                .map(this::toDto);
    }

    private UserActivitySummaryDto toDto(UserActivitySummary summary) {
        return new UserActivitySummaryDto(
                summary.getUserId(),
                summary.getTotalDecks(),
                summary.getTotalCards(),
                summary.getTotalActions(),
                summary.getLastActivityAt());
    }
}
