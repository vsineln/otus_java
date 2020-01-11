package ru.otus.homework.repository;

import ru.otus.homework.model.User;

import java.util.List;
import java.util.Optional;

public interface UserRepository {

    void saveUser(User user);

    List<User> getUsers();

    Optional<User> getByLogin(String login);
}
