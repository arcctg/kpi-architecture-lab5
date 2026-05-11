package com.flashcard.core.application.card.command;

import com.flashcard.core.application.port.EventPublisher;
import com.flashcard.core.domain.error.AccessDeniedError;
import com.flashcard.core.domain.error.EntityNotFoundError;
import com.flashcard.shared.event.CardDeleted;
import com.flashcard.core.domain.model.Card;
import com.flashcard.core.domain.model.Deck;
import com.flashcard.core.domain.model.User;
import com.flashcard.core.domain.repository.CardRepository;
import com.flashcard.core.domain.repository.DeckRepository;
import com.flashcard.core.domain.repository.UserRepository;
import com.flashcard.core.domain.valueobject.Email;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
@Transactional
public class DeleteCardCommandHandler {

    private final CardRepository cardRepository;
    private final DeckRepository deckRepository;
    private final UserRepository userRepository;
    private final EventPublisher eventPublisher;

    public DeleteCardCommandHandler(CardRepository cardRepository,
                                    DeckRepository deckRepository,
                                    UserRepository userRepository,
                                    EventPublisher eventPublisher) {
        this.cardRepository = cardRepository;
        this.deckRepository = deckRepository;
        this.userRepository = userRepository;
        this.eventPublisher = eventPublisher;
    }

    public void handle(DeleteCardCommand command) {
        User owner = userRepository.findByEmail(new Email(command.userId()))
                .orElseThrow(() -> new EntityNotFoundError("User not found"));

        Deck deck = deckRepository.findById(command.deckId())
                .orElseThrow(() -> new EntityNotFoundError("Deck not found"));

        if (!deck.isOwnedBy(owner.getId())) {
            throw new AccessDeniedError("You do not own this deck");
        }

        Card card = cardRepository.findById(command.cardId())
                .orElseThrow(() -> new EntityNotFoundError("Card not found"));

        if (!card.getDeckId().equals(deck.getId())) {
            throw new EntityNotFoundError("Card does not belong to this deck");
        }

        Long cardId = card.getId();
        cardRepository.delete(card);

        eventPublisher.publish(new CardDeleted(
                cardId, command.deckId(), owner.getId(), LocalDateTime.now()));
    }
}
