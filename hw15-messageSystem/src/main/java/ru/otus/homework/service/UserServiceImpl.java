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
    private static final Logger LOG = LoggerFactory.getLogger(UserServiceImpl.class);
    
    private final UserRepository userRepository;
    private final CacheService<String, AppUser> cacheService;

    public UserServiceImpl(UserRepository userRepository, CacheService<String, AppUser> cacheService) {
        this.userRepository = userRepository;
        this.cacheService = cacheService;
        cacheService.addListener((key, value, action) -> LoggerFactory.getLogger(
                "CacheListener").info("key:{}, value:{}, action: {}", key, value, action));
    }

    @Override
    public AppUser saveUser(AppUser user) {
        LOG.info("save user: {}", user.getLogin());
        if (cacheService.get(user.getLogin()) != null) {
            throw new UserValidationException("Login already exists");
        }
        try {
            AppUser savedUser = userRepository.save(user);
            cacheService.put(savedUser.getLogin(), savedUser);
            return savedUser;
        } catch (DuplicateKeyException e) {
            throw new UserValidationException("Login already exists", e);
        }
    }

    @Override
    public List<AppUser> getUsers() {
        LOG.info("get users");
        return userRepository.findAll();
    }
}
