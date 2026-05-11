package com.flashcard.core.application.card.command;

import com.flashcard.core.application.port.EventPublisher;
import com.flashcard.core.domain.error.AccessDeniedError;
import com.flashcard.core.domain.error.EntityNotFoundError;
import com.flashcard.shared.event.CardAdded;
import com.flashcard.core.domain.factory.CardFactory;
import com.flashcard.core.domain.model.Card;
import com.flashcard.core.domain.model.Deck;
import com.flashcard.core.domain.model.User;
import com.flashcard.core.domain.repository.CardRepository;
import com.flashcard.core.domain.repository.DeckRepository;
import com.flashcard.core.domain.repository.UserRepository;
import com.flashcard.core.domain.valueobject.CardDefinition;
import com.flashcard.core.domain.valueobject.CardTerm;
import com.flashcard.core.domain.valueobject.Email;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
@Transactional
public class AddCardCommandHandler {

    private final CardFactory cardFactory;
    private final CardRepository cardRepository;
    private final DeckRepository deckRepository;
    private final UserRepository userRepository;
    private final EventPublisher eventPublisher;

    public AddCardCommandHandler(CardFactory cardFactory,
                                 CardRepository cardRepository,
                                 DeckRepository deckRepository,
                                 UserRepository userRepository,
                                 EventPublisher eventPublisher) {
        this.cardFactory = cardFactory;
        this.cardRepository = cardRepository;
        this.deckRepository = deckRepository;
        this.userRepository = userRepository;
        this.eventPublisher = eventPublisher;
    }

    public Long handle(AddCardCommand command) {
        User owner = userRepository.findByEmail(new Email(command.userId()))
                .orElseThrow(() -> new EntityNotFoundError("User not found"));

        Deck deck = deckRepository.findById(command.deckId())
                .orElseThrow(() -> new EntityNotFoundError("Deck not found"));

        if (!deck.isOwnedBy(owner.getId())) {
            throw new AccessDeniedError("You do not own this deck");
        }

        CardTerm cardTerm = new CardTerm(command.term());
        CardDefinition cardDefinition = new CardDefinition(command.definition());
        Card card = cardFactory.create(cardTerm, cardDefinition, command.deckId());
        Card saved = cardRepository.save(card);

        eventPublisher.publish(new CardAdded(
                saved.getId(), command.deckId(), command.term(),
                command.definition(), owner.getId(), LocalDateTime.now()));

        return saved.getId();
    }
}
