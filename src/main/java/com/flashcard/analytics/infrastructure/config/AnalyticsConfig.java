package com.flashcard.analytics.infrastructure.config;

import com.flashcard.analytics.application.handler.AnalyticsEventHandler;
import com.flashcard.analytics.domain.repository.AnalyticsRepository;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class AnalyticsConfig {

    @Bean
    public AnalyticsEventHandler analyticsEventHandler(AnalyticsRepository analyticsRepository) {
        return new AnalyticsEventHandler(analyticsRepository);
    }
}
