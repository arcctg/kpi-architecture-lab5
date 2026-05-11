package com.flashcard.analytics.api;

import java.util.Optional;

public interface AnalyticsModuleApi {

    Optional<UserActivitySummaryDto> getUserSummary(Long userId);
}
