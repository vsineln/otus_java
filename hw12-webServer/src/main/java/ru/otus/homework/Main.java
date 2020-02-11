package ru.otus.homework;

import com.mongodb.MongoClient;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import org.bson.Document;
import org.mindrot.jbcrypt.BCrypt;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.otus.homework.dao.UserDao;
import ru.otus.homework.dao.UserDaoImpl;
import ru.otus.homework.model.Role;
import ru.otus.homework.model.User;
import ru.otus.homework.server.WebServer;
import ru.otus.homework.server.WebServerException;
import ru.otus.homework.server.WebServerImpl;
import ru.otus.homework.services.AuthenticateService;
import ru.otus.homework.services.AuthenticateServiceImpl;
import ru.otus.homework.services.TemplateProcessor;
import ru.otus.homework.services.TemplateProcessorImpl;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

import static ru.otus.homework.util.Constants.ADMIN;
import static ru.otus.homework.util.Constants.DB_HOST;
import static ru.otus.homework.util.Constants.DB_NAME;
import static ru.otus.homework.util.Constants.DB_PORT;
import static ru.otus.homework.util.Constants.LOG_ROUNDS;
import static ru.otus.homework.util.Constants.PROPERTIES_FILE;
import static ru.otus.homework.util.Constants.SERVER_PORT;
import static ru.otus.homework.util.Constants.TEMPLATES_DIR;
import static ru.otus.homework.util.Constants.USERS;

/*
 * http://localhost:8080/login
 * http://localhost:8080/admin
 * */
public class Main {
    private static final Logger log = LoggerFactory.getLogger(Main.class);
    private static final Properties appProperties = new Properties();

    public static void main(String[] args) {
        loadProperties();
        try (MongoClient mongoClient = new MongoClient(appProperties.getProperty(DB_HOST), Integer.parseInt(appProperties.getProperty(DB_PORT)))) {
            MongoDatabase database = mongoClient.getDatabase(appProperties.getProperty(DB_NAME));
            MongoCollection<Document> collection = database.getCollection(USERS);
            UserDao userDao = new UserDaoImpl(collection);
            createDefaultAdmin(userDao);

            AuthenticateService authenticateService = new AuthenticateServiceImpl(userDao);
            TemplateProcessor templateProcessor = new TemplateProcessorImpl(TEMPLATES_DIR);

            WebServer usersWebServer = new WebServerImpl(Integer.parseInt(appProperties.getProperty(SERVER_PORT)),
                    authenticateService,
                    userDao,
                    templateProcessor);

            usersWebServer.start();
            usersWebServer.join();
        } catch (Exception e) {
            throw new WebServerException(e.getMessage());
        }
    }

    private static void loadProperties() {
        try (InputStream input = Main.class.getClassLoader().getResourceAsStream(PROPERTIES_FILE)) {
            appProperties.load(input);
        } catch (IOException e) {
            log.error("Can not load properties");
            throw new WebServerException(e.getMessage());
        }
    }

    private static void createDefaultAdmin(UserDao userDao) {
        if (userDao.getByLogin(ADMIN).isEmpty()) {
            userDao.createUser(new User(ADMIN, ADMIN, BCrypt.hashpw(ADMIN, BCrypt.gensalt(LOG_ROUNDS)), Role.ADMIN));
        }
    }
}
