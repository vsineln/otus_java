package ru.otus.homework.service.handlers;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.otus.homework.common.Serializers;
import ru.otus.homework.dto.UserDto;
import ru.otus.homework.exception.DataHandlerException;
import ru.otus.homework.exception.UserValidationException;
import ru.otus.homework.messagesystem.Message;
import ru.otus.homework.messagesystem.RequestHandler;
import ru.otus.homework.model.AppUser;
import ru.otus.homework.service.UserService;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

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
                List<UserDto> responseList = userService.getUsers().stream().
                        map(this::toDto).collect(Collectors.toList());
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
            UserDto user = toDto(userService.saveUser(toEntity(userDto)));
            return "Saved " + user.getLogin();
        } catch (UserValidationException e) {
            LOG.error("UserDataRequestHandler ", e);
            return "Error " + e.getMessage();
        }
    }

    private AppUser toEntity(UserDto userDto) {
        return new AppUser(userDto.getName(), userDto.getLogin(), userDto.getPassword(), userDto.getRole());
    }

    private UserDto toDto(AppUser user) {
        return new UserDto(user.getName(), user.getLogin(), user.getPassword(), user.getRole());
    }
}
