package ru.otus.homework.dao;

import com.mongodb.ErrorCategory;
import com.mongodb.MongoWriteException;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoCursor;
import com.mongodb.client.model.IndexOptions;
import com.mongodb.client.model.Indexes;
import org.bson.Document;
import ru.otus.homework.model.Role;
import ru.otus.homework.model.User;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static com.mongodb.client.model.Filters.eq;
import static ru.otus.homework.util.Constants.*;

public class UserDaoImpl implements UserDao {
    private static final Logger log = LoggerFactory.getLogger(UserDaoImpl.class);
    private MongoCollection<Document> collection;

    public UserDaoImpl(MongoCollection<Document> collection) {
        this.collection = collection;
    }

    @Override
    public void createUser(User user) {
        Document userDocument = convertToDocument(user);
        collection.createIndex(Indexes.ascending(LOGIN_PARAMETER), new IndexOptions().unique(true));
        try {
            collection.insertOne(userDocument);
        } catch (MongoWriteException e) {
            log.error(e.getMessage());
            String message;
            if (e.getError().getCategory() == ErrorCategory.DUPLICATE_KEY) {
                message = "User with login '%s' already exists";
            } else {
                message = "User with login '%s' can not be created";
            }
            throw new UserDaoException(String.format(message, user.getLogin()));
        }
    }

    @Override
    public List<User> getUsers() {
        List<User> userList = new ArrayList<>();
        try (MongoCursor<Document> cursor = collection.find().iterator();) {
            while (cursor.hasNext()) {
                Document document = cursor.next();
                userList.add(convertToUser(document));
            }
        }
        return userList;
    }

    @Override
    public Optional<User> getByLogin(String login) {
        Optional<Document> documentOptional = Optional.ofNullable(collection.find(eq(LOGIN_PARAMETER, login)).first());
        return documentOptional.map(this::convertToUser);
    }

    private Document convertToDocument(User user) {
        return new Document(NAME_PARAMETER, user.getName())
                .append(LOGIN_PARAMETER, user.getLogin())
                .append(PASSWORD_PARAMETER, user.getPassword())
                .append(ROLE_PARAMETER, user.getRole().toString());
    }

    private User convertToUser(Document document) {
        return new User(document.getString(NAME_PARAMETER),
                document.getString(LOGIN_PARAMETER),
                document.getString(PASSWORD_PARAMETER),
                Role.valueOf(document.getString(ROLE_PARAMETER)));
    }
}
