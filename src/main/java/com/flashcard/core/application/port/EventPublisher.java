package com.flashcard.core.application.port;

public interface EventPublisher {

    void publish(Object event);
}
