package com.flashcard.core.infrastructure.persistence.mapper;

import com.flashcard.core.domain.model.Card;
import com.flashcard.core.domain.valueobject.CardDefinition;
import com.flashcard.core.domain.valueobject.CardTerm;
import com.flashcard.core.infrastructure.persistence.entity.CardEntity;
import com.flashcard.core.infrastructure.persistence.entity.DeckEntity;

public class CardMapper {

    private CardMapper() {
    }

    public static Card toDomain(CardEntity entity) {
        return new Card(
                entity.getId(),
                new CardTerm(entity.getTerm()),
                new CardDefinition(entity.getDefinition()),
                entity.getDeck().getId(),
                entity.getCreatedAt(),
                entity.getUpdatedAt()
        );
    }

    public static CardEntity toEntity(Card domain, DeckEntity deck) {
        CardEntity entity = new CardEntity();
        entity.setId(domain.getId());
        entity.setTerm(domain.getTerm().value());
        entity.setDefinition(domain.getDefinition().value());
        entity.setDeck(deck);
        entity.setCreatedAt(domain.getCreatedAt());
        entity.setUpdatedAt(domain.getUpdatedAt());
        return entity;
    }

    public static void updateEntity(CardEntity entity, Card domain) {
        entity.setTerm(domain.getTerm().value());
        entity.setDefinition(domain.getDefinition().value());
    }
}
