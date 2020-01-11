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
import ru.otus.homework.exception.UserValidationException;
import ru.otus.homework.model.User;
import ru.otus.homework.service.UserService;

import javax.validation.Valid;
import java.util.List;

@Controller
public class UserController {
    private static final Logger logger = LoggerFactory.getLogger(UserController.class);
    private UserService userService;
    private BCryptPasswordEncoder passwordEncoder;

    public UserController(UserService userService, BCryptPasswordEncoder passwordEncoder) {
        this.userService = userService;
        this.passwordEncoder = passwordEncoder;
    }

    @PostMapping("/user/save")
    public String saveUser(@Valid User user, BindingResult result) {
        logger.info("save user {}", user.getLogin());
        if (!result.hasErrors()) {
            user.setPassword(passwordEncoder.encode(user.getPassword()));
            try {
                userService.saveUser(user);
            } catch (UserValidationException e) {
                result.rejectValue("login", "error.user", e.getMessage());
            }
        }
        return "userCreate";
    }

    @GetMapping({"/", "/user/list"})
    public String getUsersList(Model model) {
        List<User> users = userService.getUsers();
        model.addAttribute("users", users);
        return "userList";
    }

    @GetMapping("/admin")
    public String saveUser(@ModelAttribute User user) {
        return "userCreate";
    }
}
