package com.flashcard.core.api;

import java.util.Optional;

public interface CoreModuleApi {

    Optional<DeckSummaryDto> getDeckSummary(Long deckId);

    long countDecksByOwner(Long ownerId);

    long countCardsByDeck(Long deckId);

    Long resolveUserId(String email);
}
