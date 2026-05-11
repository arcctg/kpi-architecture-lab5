package com.flashcard.core.infrastructure.config;

import com.flashcard.core.domain.factory.CardFactory;
import com.flashcard.core.domain.factory.DeckFactory;
import com.flashcard.core.domain.repository.CardRepository;
import com.flashcard.core.domain.repository.DeckRepository;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class DomainConfig {

    @Bean
    public DeckFactory deckFactory(DeckRepository deckRepository) {
        return new DeckFactory(deckRepository);
    }

    @Bean
    public CardFactory cardFactory(CardRepository cardRepository, DeckRepository deckRepository) {
        return new CardFactory(cardRepository, deckRepository);
    }
}
