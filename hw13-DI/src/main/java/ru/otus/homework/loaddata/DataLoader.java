package ru.otus.homework.loaddata;

import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Component;
import ru.otus.homework.model.Role;
import ru.otus.homework.model.AppUser;
import ru.otus.homework.repository.UserRepository;

/**
 * Create default 'admin' user in data base
 */
@Component
public class DataLoader implements ApplicationListener<ContextRefreshedEvent> {
    private static final String ADMIN = "admin";
    private UserRepository userRepository;
    private BCryptPasswordEncoder passwordEncoder;

    public DataLoader(UserRepository userRepository, BCryptPasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public void onApplicationEvent(final ContextRefreshedEvent event) {
        if (userRepository.getByLogin(ADMIN).isEmpty()) {
            userRepository.saveUser(new AppUser(ADMIN, ADMIN, passwordEncoder.encode(ADMIN), Role.ADMIN));
        }
    }
}
