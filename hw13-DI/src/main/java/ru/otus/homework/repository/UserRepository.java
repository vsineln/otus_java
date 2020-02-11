package ru.otus.homework.repository;

import ru.otus.homework.model.AppUser;

import java.util.List;
import java.util.Optional;

public interface UserRepository {

    void saveUser(AppUser user);

    List<AppUser> getUsers();

    Optional<AppUser> getByLogin(String login);
}
