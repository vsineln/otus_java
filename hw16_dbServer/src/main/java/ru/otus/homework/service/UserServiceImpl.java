package ru.otus.homework.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.stereotype.Service;
import ru.otus.homework.cache.CacheService;
import ru.otus.homework.dto.UserDto;
import ru.otus.homework.exception.UserValidationException;
import ru.otus.homework.model.AppUser;
import ru.otus.homework.repository.UserRepository;

import java.util.List;
import java.util.stream.Collectors;

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
    public UserDto saveUser(UserDto user) {
        LOG.info("save user: {}", user.getLogin());
        if (cacheService.get(user.getLogin()) != null) {
            throw new UserValidationException("Login already exists");
        }
        try {
            AppUser savedUser = userRepository.save(toEntity(user));
            cacheService.put(savedUser.getLogin(), savedUser);
            return toDto(savedUser);
        } catch (DuplicateKeyException e) {
            throw new UserValidationException("Login already exists", e);
        }
    }

    @Override
    public List<UserDto> getUsers() {
        LOG.info("get users");
        return userRepository.findAll().stream().map(this::toDto).collect(Collectors.toList());
    }

    private AppUser toEntity(UserDto userDto) {
        return new AppUser(userDto.getName(), userDto.getLogin(), userDto.getPassword(), userDto.getRole());
    }

    private UserDto toDto(AppUser user) {
        return new UserDto(user.getName(), user.getLogin(), user.getPassword(), user.getRole());
    }
}
