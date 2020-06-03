package ru.otus.homework.service;

import ru.otus.homework.model.AppUser;

import java.util.List;

public interface UserService {

    AppUser saveUser(AppUser user);

    List<AppUser> getUsers();
}
