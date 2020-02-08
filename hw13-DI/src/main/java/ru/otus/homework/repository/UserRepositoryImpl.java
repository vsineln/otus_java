package ru.otus.homework.repository;

import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Repository;
import ru.otus.homework.model.UserDoc;

import java.util.List;
import java.util.Optional;

@Repository
public class UserRepositoryImpl implements UserRepository {
    private MongoTemplate mongoTemplate;

    public UserRepositoryImpl(MongoTemplate mongoTemplate) {
        this.mongoTemplate = mongoTemplate;
    }

    @Override
    public void saveUser(UserDoc user) {
        mongoTemplate.insert(user, "users");
    }

    @Override
    public List<UserDoc> getUsers() {
        return mongoTemplate.findAll(UserDoc.class);
    }

    @Override
    public Optional<UserDoc> getByLogin(String login) {
        Query query = new Query();
        query.addCriteria(Criteria.where("login").is(login));
        return Optional.ofNullable(mongoTemplate.findOne(query, UserDoc.class));
    }
}
