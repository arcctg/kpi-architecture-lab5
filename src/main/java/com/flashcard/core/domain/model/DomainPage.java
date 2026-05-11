package com.flashcard.core.domain.model;

import java.util.List;

public record DomainPage<T>(
        List<T> content,
        int page,
        int size,
        long totalElements,
        int totalPages
) {

    public DomainPage {
        content = List.copyOf(content);
    }
}
