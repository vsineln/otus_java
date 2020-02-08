package ru.otus.homework.repository;

import ru.otus.homework.model.UserDoc;

import java.util.List;
import java.util.Optional;

public interface UserRepository {

    void saveUser(UserDoc user);

    List<UserDoc> getUsers();

    Optional<UserDoc> getByLogin(String login);
}
