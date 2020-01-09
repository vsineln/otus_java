package ru.otus.homework;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.otus.homework.api.dao.ObjectDao;
import ru.otus.homework.api.model.Account;
import ru.otus.homework.api.model.User;
import ru.otus.homework.api.service.DbService;
import ru.otus.homework.cache.HwCache;
import ru.otus.homework.cache.MyCache;
import ru.otus.homework.dbService.DbServiceWithCache;
import ru.otus.homework.jdbc.dao.ObjectDaoJdbc;
import ru.otus.homework.jdbc.sessionmanager.SessionManagerJdbc;

import javax.sql.DataSource;
import java.math.BigDecimal;
import java.sql.PreparedStatement;
import java.sql.SQLException;

import static org.junit.jupiter.api.Assertions.assertEquals;

class JdbcTemplateTest {
    private DbService dbService;
    private DataSource dataSource;
    private User user1 = new User("name1", 20);
    private User user2 = new User("name2", 30);
    private Account account1 = new Account("account1", new BigDecimal(111));
    private Account account2 = new Account("account2", new BigDecimal(333));
    private Logger logger = LoggerFactory.getLogger(JdbcTemplateTest.class);

    @BeforeEach
    public void init() throws SQLException {
        dataSource = new DataSourceH2();
        SessionManagerJdbc sessionManager = new SessionManagerJdbc(dataSource);
        ObjectDao objectDao = new ObjectDaoJdbc(sessionManager);
        HwCache<String, Object> cache = new MyCache<>();
        cache.addListener((key, value, action) -> logger.info("key:{}, value:{}, action: {}", key, value, action));
        dbService = new DbServiceWithCache(objectDao, cache);
        createTables(dataSource);
    }

    @AfterEach
    public void tearDown() throws SQLException {
        dropTable(dataSource);
    }

    @Test
    void createAndLoadUsers() {
        long id1 = dbService.saveObject(user1);
        long id2 = dbService.saveObject(user2);

        User returnedUser1 = (User) dbService.getObject(id1, User.class).get();
        User returnedUser2 = (User) dbService.getObject(id2, User.class).get();

        assertEquals(user1.getName(), returnedUser1.getName());
        assertEquals(user1.getAge(), returnedUser1.getAge());
        assertEquals(user2.getName(), returnedUser2.getName());
        assertEquals(user2.getAge(), returnedUser2.getAge());
    }

    @Test
    void createAndLoadAccounts() {
        long id1 = dbService.saveObject(account1);
        long id2 = dbService.saveObject(account2);

        Account returnedAccount1 = (Account) dbService.getObject(id1, Account.class).get();
        Account returnedAccount2 = (Account) dbService.getObject(id2, Account.class).get();

        assertEquals(account1.getType(), returnedAccount1.getType());
        assertEquals(account1.getRest(), returnedAccount1.getRest());
        assertEquals(account2.getType(), returnedAccount2.getType());
        assertEquals(account2.getRest(), returnedAccount2.getRest());
    }

    @Test
    void createAndUpdateUser() {
        long id = dbService.saveObject(user1);

        dbService.updateObject(new User(id, "updatedName", 100));

        User returnedUser1 = (User) dbService.getObject(id, User.class).get();
        assertEquals("updatedName", returnedUser1.getName());
        assertEquals(100, returnedUser1.getAge().intValue());
    }

    @Test
    void createAndUpdateAccount() {
        long id = dbService.saveObject(account1);

        dbService.updateObject(new Account(id, "updatedAccount", new BigDecimal(1000)));

        Account returnedAccount1 = (Account) dbService.getObject(id, Account.class).get();
        assertEquals("updatedAccount", returnedAccount1.getType());
        assertEquals(1000, returnedAccount1.getRest().intValue());
    }

    @Test
    void createOrUpdateUser() {
        long id = dbService.saveObject(user1);
        dbService.createOrUpdate(new User(id, "name1", 40));

        User returnedUser1 = (User) dbService.getObject(id, User.class).get();
        assertEquals("name1", returnedUser1.getName());
        assertEquals(40, returnedUser1.getAge().intValue());

        id += 1;
        dbService.createOrUpdate(new User(id, "name", 37));
        User returnedUser2 = (User) dbService.getObject(id, User.class).get();
        assertEquals("name", returnedUser2.getName());
        assertEquals(37, returnedUser2.getAge().intValue());
    }

    @Test
    void createOrUpdateAccount() {
        long id = dbService.saveObject(account1);
        dbService.createOrUpdate(new Account(id, "account1", new BigDecimal(222)));

        Account returnedAccount1 = (Account) dbService.getObject(id, Account.class).get();
        assertEquals("account1", returnedAccount1.getType());
        assertEquals(222, returnedAccount1.getRest().intValue());

        id = id + 1;
        dbService.createOrUpdate(new Account(id, "account3", new BigDecimal(444)));
        Account returnedAccount2 = (Account) dbService.getObject(id, Account.class).get();
        assertEquals("account3", returnedAccount2.getType());
        assertEquals(444, returnedAccount2.getRest().intValue());
    }

    private void createTables(DataSource dataSource) throws SQLException {
        try (PreparedStatement pst1 = dataSource.getConnection().prepareStatement(
                "create table User(id bigint(20) NOT NULL auto_increment, name varchar(255), age int(3))");
             PreparedStatement pst2 = dataSource.getConnection().prepareStatement(
                     "create table Account(no bigint(20) NOT NULL auto_increment, type varchar(255), rest number)")) {
            pst1.execute();
            pst2.execute();
        }
    }

    private void dropTable(DataSource dataSource) throws SQLException {
        try (PreparedStatement pst1 = dataSource.getConnection().prepareStatement("drop table if exists User");
             PreparedStatement pst2 = dataSource.getConnection().prepareStatement("drop table if exists Account")) {
            pst1.executeUpdate();
            pst2.executeUpdate();
        }
    }
}
