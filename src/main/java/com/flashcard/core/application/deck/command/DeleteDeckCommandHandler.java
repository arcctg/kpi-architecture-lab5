package com.flashcard.core.application.deck.command;

import com.flashcard.core.application.port.EventPublisher;
import com.flashcard.core.domain.error.AccessDeniedError;
import com.flashcard.core.domain.error.EntityNotFoundError;
import com.flashcard.shared.event.DeckDeleted;
import com.flashcard.core.domain.model.Deck;
import com.flashcard.core.domain.model.User;
import com.flashcard.core.domain.repository.DeckRepository;
import com.flashcard.core.domain.repository.UserRepository;
import com.flashcard.core.domain.valueobject.Email;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
@Transactional
public class DeleteDeckCommandHandler {

    private final DeckRepository deckRepository;
    private final UserRepository userRepository;
    private final EventPublisher eventPublisher;

    public DeleteDeckCommandHandler(DeckRepository deckRepository,
                                    UserRepository userRepository,
                                    EventPublisher eventPublisher) {
        this.deckRepository = deckRepository;
        this.userRepository = userRepository;
        this.eventPublisher = eventPublisher;
    }

    public void handle(DeleteDeckCommand command) {
        User owner = userRepository.findByEmail(new Email(command.userId()))
                .orElseThrow(() -> new EntityNotFoundError("User not found"));

        Deck deck = deckRepository.findById(command.deckId())
                .orElseThrow(() -> new EntityNotFoundError("Deck not found"));

        if (!deck.isOwnedBy(owner.getId())) {
            throw new AccessDeniedError("You do not own this deck");
        }

        Long deckId = deck.getId();
        deckRepository.delete(deck);

        eventPublisher.publish(new DeckDeleted(
                deckId, owner.getId(), LocalDateTime.now()));
    }
}
