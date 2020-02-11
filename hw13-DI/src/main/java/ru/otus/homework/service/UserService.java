package ru.otus.homework.service;

import ru.otus.homework.model.AppUser;

import java.util.List;

public interface UserService {

    void saveUser(AppUser user);

    List<AppUser> getUsers();
}
