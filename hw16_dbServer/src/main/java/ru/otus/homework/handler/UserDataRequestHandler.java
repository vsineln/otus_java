package ru.otus.homework.handler;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.otus.homework.common.Serializers;
import ru.otus.homework.dto.UserDto;
import ru.otus.homework.exception.DataHandlerException;
import ru.otus.homework.exception.UserValidationException;

import ru.otus.homework.ms.Message;
import ru.otus.homework.ms.RequestHandler;
import ru.otus.homework.service.UserService;

import java.util.List;
import java.util.Optional;

public class UserDataRequestHandler implements RequestHandler {
    private static final Logger LOG = LoggerFactory.getLogger(UserDataRequestHandler.class);
    private final UserService userService;

    public UserDataRequestHandler(UserService userService) {
        this.userService = userService;
    }

    @Override
    public Optional<Message> handle(Message msg) {
        LOG.info("handle msg: {}", msg);

        String messageType = msg.getType();
        Message returnMessage;
        switch (messageType) {
            case "UserSave":
                UserDto user = Serializers.deserialize(msg.getPayload(), UserDto.class);
                returnMessage = new Message(msg.getTo(), msg.getFrom(), msg.getId(),
                        msg.getType(), Serializers.serialize(getMessage(user)));
                break;
            case "UserList":
                List<UserDto> responseList = userService.getUsers();
                returnMessage = new Message(msg.getTo(), msg.getFrom(), msg.getId(),
                        msg.getType(), Serializers.serialize(responseList));
                break;
            default:
                throw new DataHandlerException(String.format("Unknown message type %s", messageType));
        }
        return Optional.of(returnMessage);
    }

    private String getMessage(UserDto userDto) {
        try {
            UserDto savedUser = userService.saveUser(userDto);
            return "Saved " + savedUser.getLogin();
        } catch (UserValidationException e) {
            LOG.error("UserDataRequestHandler ", e);
            return "Error " + e.getMessage();
        }
    }
}
