package ru.otus.homework.service;

import ru.otus.homework.dto.UserDto;

import java.util.List;

public interface UserService {

    UserDto saveUser(UserDto user);

    List<UserDto> getUsers();
}
