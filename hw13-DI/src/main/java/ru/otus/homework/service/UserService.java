package ru.otus.homework.service;

import ru.otus.homework.model.UserDoc;

import java.util.List;

public interface UserService {

    void saveUser(UserDoc user);

    List<UserDoc> getUsers();
}
