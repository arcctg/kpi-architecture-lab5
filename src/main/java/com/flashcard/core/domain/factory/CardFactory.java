package com.flashcard.core.domain.factory;

import com.flashcard.core.domain.error.DuplicateError;
import com.flashcard.core.domain.error.EntityNotFoundError;
import com.flashcard.core.domain.model.Card;
import com.flashcard.core.domain.repository.CardRepository;
import com.flashcard.core.domain.repository.DeckRepository;
import com.flashcard.core.domain.valueobject.CardDefinition;
import com.flashcard.core.domain.valueobject.CardTerm;

public class CardFactory {

    private final CardRepository cardRepository;
    private final DeckRepository deckRepository;

    public CardFactory(CardRepository cardRepository, DeckRepository deckRepository) {
        this.cardRepository = cardRepository;
        this.deckRepository = deckRepository;
    }

    public Card create(CardTerm term, CardDefinition definition, Long deckId) {
        if (deckRepository.findById(deckId).isEmpty()) {
            throw new EntityNotFoundError("Deck not found");
        }

        if (cardRepository.existsByTermAndDeckId(term, deckId)) {
            throw new DuplicateError(
                    "Card with term '" + term.value() + "' already exists in this deck");
        }

        return Card.create(term, definition, deckId);
    }
}
