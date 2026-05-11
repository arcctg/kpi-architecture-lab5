package com.flashcard.core.application.deck.query;

import com.flashcard.core.application.dto.DeckResult;
import com.flashcard.core.domain.model.DomainPage;

import java.util.Optional;

public interface DeckReadRepository {
    Optional<DeckResult> findByIdAndUserId(Long id, String userId);
    DomainPage<DeckResult> findAllByUserId(String userId, int page, int size);
}
