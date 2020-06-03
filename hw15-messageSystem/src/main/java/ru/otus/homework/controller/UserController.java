package ru.otus.homework.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import ru.otus.homework.dto.UserDto;
import ru.otus.homework.front.FrontendService;

import java.util.List;

@Controller
public class UserController {
    private static final Logger LOG = LoggerFactory.getLogger(UserController.class);

    private final FrontendService frontendService;
    private final SimpMessagingTemplate template;

    public UserController(FrontendService frontendService, SimpMessagingTemplate template) {
        this.frontendService = frontendService;
        this.template = template;
    }

    @GetMapping("/user/save")
    public String userSaveView() {
        return "userSave";
    }

    @MessageMapping("/user/save")
    public void saveUser(@Payload UserDto userDto) {
        LOG.info("save user {}", userDto);

        frontendService.saveUser(userDto, (String message) -> {
            LOG.info("response data:{}", message);
            template.convertAndSend("/topic/response/user/save", message);
        });
    }

    @GetMapping("/user/list")
    public String userListView() {
        return "userList";
    }

    @MessageMapping("/user/list")
    public void userList() {
        LOG.info("get list of users");

        frontendService.getUserList((List<UserDto> data) -> {
            LOG.info("response data:{}", data);
            template.convertAndSend("/topic/response/user/list", data);
        });
    }
}
