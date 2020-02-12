package ru.otus.homework.dao;

import ru.otus.homework.model.User;

import java.util.List;
import java.util.Optional;

public interface UserDao {

    void createUser(User user);

    List<User> getUsers();

    Optional<User> getByLogin(String login);
}
