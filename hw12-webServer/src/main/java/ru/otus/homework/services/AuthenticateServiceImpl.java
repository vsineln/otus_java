package ru.otus.homework.services;

import org.mindrot.jbcrypt.BCrypt;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.otus.homework.dao.UserDao;
import ru.otus.homework.model.Role;
import ru.otus.homework.model.User;
import ru.otus.homework.server.WebServerException;

import java.util.Optional;

public class AuthenticateServiceImpl implements AuthenticateService {
    private static final Logger log = LoggerFactory.getLogger(AuthenticateServiceImpl.class);
    private UserDao userDao;

    public AuthenticateServiceImpl(UserDao userDao) {
        this.userDao = userDao;
    }

    @Override
    public void authenticateAdmin(String login, String password) {
        log.info("authenticate as admin {}", login);
        Optional<User> user = userDao.getByLogin(login);
        if (user.isEmpty() || !BCrypt.checkpw(password, user.get().getPassword())) {
            throw new WebServerException("Wrong password or login");
        }
        if (!Role.ADMIN.equals(user.get().getRole())) {
            throw new WebServerException("Wrong access rights");
        }
    }
}
