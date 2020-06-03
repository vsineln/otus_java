package ru.otus.homework.handler;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.otus.homework.common.Serializers;
import ru.otus.homework.dto.UserDto;
import ru.otus.homework.exception.DataHandlerException;
import ru.otus.homework.ms.Message;
import ru.otus.homework.ms.RequestHandler;
import ru.otus.homework.service.FrontendService;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public class UserDataResponseHandler implements RequestHandler {
    private static final Logger LOG = LoggerFactory.getLogger(UserDataResponseHandler.class);
    private final FrontendService frontendService;

    public UserDataResponseHandler(FrontendService frontendService) {
        this.frontendService = frontendService;
    }

    @Override
    public Optional<Message> handle(Message msg) {
        LOG.info("new message:{}", msg);
        try {
            UUID sourceMessageId = msg.getSourceMessageId().
                    orElseThrow(() -> new RuntimeException("Not found sourceMsg for message:" + msg.getId()));
            String messageType = msg.getType();
            switch (messageType) {
                case "UserSave":
                    String message = Serializers.deserialize(msg.getPayload(), String.class);
                    frontendService.takeConsumer(sourceMessageId, String.class).
                            ifPresent(consumer -> consumer.accept(message));
                    break;
                case "UserList":
                    List<UserDto> users = Serializers.deserialize(msg.getPayload(), List.class);
                    frontendService.takeConsumer(sourceMessageId, List.class).
                            ifPresent(consumer -> consumer.accept(users));
                    break;
                default:
                    throw new DataHandlerException(String.format("Unknown message type %s", messageType));
            }
        } catch (Exception ex) {
            LOG.error("msg: {}", ex.getMessage());
        }
        return Optional.empty();
    }
}
