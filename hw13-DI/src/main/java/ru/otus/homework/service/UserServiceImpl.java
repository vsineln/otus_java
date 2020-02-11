package ru.otus.homework.service;

import org.springframework.dao.DuplicateKeyException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import ru.otus.homework.model.AppUser;
import ru.otus.homework.repository.UserRepository;
import ru.otus.homework.cache.CacheService;
import ru.otus.homework.exception.UserValidationException;

import java.util.List;

@Service
public class UserServiceImpl implements UserService {
    private static final Logger logger = LoggerFactory.getLogger(UserServiceImpl.class);
    private UserRepository userRepository;
    private CacheService<String, AppUser> cacheService;

    public UserServiceImpl(UserRepository userRepository, CacheService<String, AppUser> cacheService) {
        this.userRepository = userRepository;
        this.cacheService = cacheService;
        cacheService.addListener((key, value, action) -> LoggerFactory.getLogger(
                "CacheListener").info("key:{}, value:{}, action: {}", key, value, action));
    }

    @Override
    public void saveUser(AppUser user) {
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
    public List<AppUser> getUsers() {
        logger.debug("get users");
        return userRepository.getUsers();
    }
}
