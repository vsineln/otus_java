package ru.otus.homework.service;

import ru.otus.homework.model.User;

import java.util.List;

public interface UserService {

    void saveUser(User user);

    List<User> getUsers();

    boolean loginAdmin(String login, String password);
}
