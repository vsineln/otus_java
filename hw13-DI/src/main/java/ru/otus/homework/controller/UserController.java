package ru.otus.homework.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import ru.otus.homework.dto.UserDto;
import ru.otus.homework.exception.UserValidationException;
import ru.otus.homework.model.UserDoc;
import ru.otus.homework.service.UserService;

import javax.validation.Valid;
import java.util.List;
import java.util.stream.Collectors;

@Controller
public class UserController {
    private static final Logger logger = LoggerFactory.getLogger(UserController.class);
    private UserService userService;
    private BCryptPasswordEncoder passwordEncoder;

    public UserController(UserService userService, BCryptPasswordEncoder passwordEncoder) {
        this.userService = userService;
        this.passwordEncoder = passwordEncoder;
    }

    @GetMapping({"/", "/user/list"})
    public String userListView(Model model) {
        List<UserDto> users = userService.getUsers().stream().
                map(this::toDto).collect(Collectors.toList());
        model.addAttribute("users", users);
        return "userList";
    }

    @GetMapping("/user/save")
    public String userSaveView(@ModelAttribute UserDto userDto) {
        return "userSave";
    }

    @PostMapping("/user/save")
    public String saveUser(@Valid UserDto userDto, BindingResult result) {
        logger.info("save user {}", userDto.getLogin());
        if (!result.hasErrors()) {
            try {
                userService.saveUser(toEntity(userDto));
            } catch (UserValidationException e) {
                result.rejectValue("login", "error.user", e.getMessage());
            }
        }
        return "userSave";
    }

    private UserDoc toEntity(UserDto userDto) {
        return new UserDoc(userDto.getName(), userDto.getLogin(), passwordEncoder.encode(userDto.getPassword()), userDto.getRole());
    }

    private UserDto toDto(UserDoc user) {
        return new UserDto(user.getName(), user.getLogin(), "", user.getRole());
    }
}
