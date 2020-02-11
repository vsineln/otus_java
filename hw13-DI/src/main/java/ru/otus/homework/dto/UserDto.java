package ru.otus.homework.dto;

import ru.otus.homework.model.Role;

import javax.validation.constraints.Size;

public class UserDto {
    private String name;
    @Size(min = 4, message = "Login should be at least 4 characters")
    private String login;
    @Size(min = 4, message = "Password should be at least 4 characters")
    private String password;
    private Role role;

    public UserDto(String name, String login, String password, Role role) {
        this.name = name;
        this.login = login;
        this.password = password;
        this.role = role;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getLogin() {
        return login;
    }

    public void setLogin(String login) {
        this.login = login;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public Role getRole() {
        return role;
    }

    public void setRole(Role role) {
        this.role = role;
    }
}
