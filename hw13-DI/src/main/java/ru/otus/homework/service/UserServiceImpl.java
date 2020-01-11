package ru.otus.homework.service;

import org.springframework.dao.DuplicateKeyException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import ru.otus.homework.model.Role;
import ru.otus.homework.model.User;
import ru.otus.homework.repository.UserRepository;
import ru.otus.homework.cache.CacheService;
import ru.otus.homework.exception.UserValidationException;

import java.util.List;
import java.util.Optional;

@Service
public class UserServiceImpl implements UserService {
    private static final Logger logger = LoggerFactory.getLogger(UserServiceImpl.class);
    private UserRepository userRepository;
    private CacheService<String, User> cacheService;

    public UserServiceImpl(UserRepository userRepository, CacheService<String, User> cacheService) {
        this.userRepository = userRepository;
        this.cacheService = cacheService;
        cacheService.addListener((key, value, action) -> LoggerFactory.getLogger(
                "CacheListener").info("key:{}, value:{}, action: {}", key, value, action));
    }

    @Override
    public void saveUser(User user) {
        logger.debug("save user: {}", user.getLogin());
        if (cacheService.get(user.getLogin()) != null) {
            throw new UserValidationException("Login already exists");
        }
        try {
            userRepository.saveUser(user);
        } catch (DuplicateKeyException e) {
            logger.error(e.toString());
            throw new UserValidationException("Login already exist");
        }
        cacheService.put(user.getLogin(), user);
    }

    @Override
    public List<User> getUsers() {
        logger.debug("get users");
        return userRepository.getUsers();
    }

    @Override
    public boolean loginAdmin(String login, String password) {
        logger.debug("login as admin: {}", login);
        Optional<User> optionalUser;
        if (cacheService.get(login) != null) {
            optionalUser = Optional.ofNullable(cacheService.get(login));
        } else {
            optionalUser = userRepository.getByLogin(login);
        }
        return optionalUser.map(user -> password.equals(user.getPassword()) && Role.ADMIN.equals(user.getRole())).orElse(false);
    }
}
