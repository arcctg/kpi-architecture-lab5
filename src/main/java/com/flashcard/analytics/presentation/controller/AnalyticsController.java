package com.flashcard.analytics.presentation.controller;

import com.flashcard.analytics.api.AnalyticsModuleApi;
import com.flashcard.analytics.api.UserActivitySummaryDto;
import com.flashcard.analytics.presentation.dto.UserActivitySummaryResponse;
import com.flashcard.core.api.CoreModuleApi;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/analytics")
public class AnalyticsController {

    private final AnalyticsModuleApi analyticsModuleApi;
    private final CoreModuleApi coreModuleApi;

    public AnalyticsController(AnalyticsModuleApi analyticsModuleApi,
                               CoreModuleApi coreModuleApi) {
        this.analyticsModuleApi = analyticsModuleApi;
        this.coreModuleApi = coreModuleApi;
    }

    @GetMapping("/users/me/summary")
    public ResponseEntity<UserActivitySummaryResponse> mySummary(Authentication auth) {
        Long userId = coreModuleApi.resolveUserId(auth.getName());
        return analyticsModuleApi.getUserSummary(userId)
                .map(this::toResponse)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.ok(emptyResponse(userId)));
    }

    private UserActivitySummaryResponse toResponse(UserActivitySummaryDto dto) {
        return new UserActivitySummaryResponse(
                dto.userId(), dto.totalDecks(), dto.totalCards(),
                dto.totalActions(), dto.lastActivityAt());
    }

    private UserActivitySummaryResponse emptyResponse(Long userId) {
        return new UserActivitySummaryResponse(userId, 0, 0, 0, null);
    }
}
