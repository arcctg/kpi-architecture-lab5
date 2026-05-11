package com.flashcard.core.api;

import com.flashcard.core.domain.error.EntityNotFoundError;
import com.flashcard.core.domain.model.Deck;
import com.flashcard.core.domain.repository.CardRepository;
import com.flashcard.core.domain.repository.DeckRepository;
import com.flashcard.core.domain.repository.UserRepository;
import com.flashcard.core.domain.valueobject.Email;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class DefaultCoreModuleApi implements CoreModuleApi {

    private final DeckRepository deckRepository;
    private final CardRepository cardRepository;
    private final UserRepository userRepository;

    public DefaultCoreModuleApi(DeckRepository deckRepository,
                                CardRepository cardRepository,
                                UserRepository userRepository) {
        this.deckRepository = deckRepository;
        this.cardRepository = cardRepository;
        this.userRepository = userRepository;
    }

    @Override
    public Long resolveUserId(String email) {
        return userRepository.findByEmail(new Email(email))
                .orElseThrow(() -> new EntityNotFoundError("User not found"))
                .getId();
    }

    @Override
    public Optional<DeckSummaryDto> getDeckSummary(Long deckId) {
        return deckRepository.findById(deckId)
                .map(this::toSummary);
    }

    @Override
    public long countDecksByOwner(Long ownerId) {
        return deckRepository.findByOwnerId(ownerId, 0, 1).totalElements();
    }

    @Override
    public long countCardsByDeck(Long deckId) {
        return cardRepository.countByDeckId(deckId);
    }

    private DeckSummaryDto toSummary(Deck deck) {
        return new DeckSummaryDto(
                deck.getId(),
                deck.getTitle().value(),
                deck.getDescription(),
                deck.getCardCount(),
                deck.getCreatedAt()
        );
    }
}
