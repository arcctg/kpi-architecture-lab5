package com.flashcard.core.domain.repository;

import com.flashcard.core.domain.model.Deck;
import com.flashcard.core.domain.model.DomainPage;
import com.flashcard.core.domain.valueobject.DeckTitle;
import java.util.Optional;

public interface DeckRepository {

    Deck save(Deck deck);

    Optional<Deck> findById(Long id);

    void delete(Deck deck);

    boolean existsByTitleAndOwnerId(DeckTitle title, Long ownerId);

    DomainPage<Deck> findByOwnerId(Long ownerId, int page, int size);
}
