package ru.otus.homework.front;

import ru.otus.homework.dto.UserDto;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.function.Consumer;

public interface FrontendService {

    void saveUser(UserDto userDto, Consumer<String> dataConsumer);

    void getUserList(Consumer<List<UserDto>> dataConsumer);

    <T> Optional<Consumer<T>> takeConsumer(UUID sourceMessageId, Class<T> tClass);
}
