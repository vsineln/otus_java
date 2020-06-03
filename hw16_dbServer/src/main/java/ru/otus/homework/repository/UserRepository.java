package ru.otus.homework.repository;

import org.springframework.data.mongodb.repository.MongoRepository;
import ru.otus.homework.model.AppUser;

import java.util.Optional;

public interface UserRepository extends MongoRepository<AppUser, String> {

    Optional<AppUser> getByLogin(String login);
}
