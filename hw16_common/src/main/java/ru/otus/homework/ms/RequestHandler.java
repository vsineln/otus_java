package ru.otus.homework.ms;

import java.util.Optional;

public interface RequestHandler {
    Optional<Message> handle(Message msg);
}
