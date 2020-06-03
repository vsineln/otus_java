package ru.otus.homework.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.otus.homework.dto.UserDto;
import ru.otus.homework.ms.Message;
import ru.otus.homework.ms.MessageType;
import ru.otus.homework.ms.MsClient;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Consumer;

public class FrontendServiceImpl implements FrontendService {
    private static final Logger LOG = LoggerFactory.getLogger(FrontendServiceImpl.class);
    private final Map<UUID, Consumer<?>> consumerMap = new ConcurrentHashMap<>();

    private final MsClient msClient;

    public FrontendServiceImpl(MsClient msClient) {
        this.msClient = msClient;
    }

    @Override
    public void saveUser(UserDto userDto, Consumer<String> dataConsumer) {
        Message outMsg = msClient.produceMessage(null, userDto, MessageType.USER_SAVE);
        consumerMap.put(outMsg.getId(), dataConsumer);
        msClient.sendMessage(outMsg);
    }

    @Override
    public void getUserList(Consumer<List<UserDto>> dataConsumer) {
        Message outMsg = msClient.produceMessage(null, null, MessageType.USER_LIST);
        consumerMap.put(outMsg.getId(), dataConsumer);
        msClient.sendMessage(outMsg);
    }

    @Override
    public <T> Optional<Consumer<T>> takeConsumer(UUID sourceMessageId, Class<T> tClass) {
        Consumer<T> consumer = (Consumer<T>) consumerMap.remove(sourceMessageId);
        if (consumer == null) {
            LOG.warn("consumer not found for:{}", sourceMessageId);
            return Optional.empty();
        }
        return Optional.of(consumer);
    }
}
