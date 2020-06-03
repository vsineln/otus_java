package ru.otus.homework.loaddata;

import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.stereotype.Component;
import ru.otus.homework.model.Role;
import ru.otus.homework.model.AppUser;
import ru.otus.homework.repository.UserRepository;

/**
 * Create default user in data base
 */
@Component
public class DataLoader implements ApplicationListener<ContextRefreshedEvent> {
    private static final String ADMIN = "admin";
    private final UserRepository userRepository;

    public DataLoader(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public void onApplicationEvent(final ContextRefreshedEvent event) {
        if (userRepository.getByLogin(ADMIN).isEmpty()) {
            userRepository.save(new AppUser(ADMIN, ADMIN, ADMIN, Role.ADMIN));
        }
    }
}
