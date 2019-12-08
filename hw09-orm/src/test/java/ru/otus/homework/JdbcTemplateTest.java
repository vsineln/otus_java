package ru.otus.homework;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.otus.homework.api.dao.ObjectDao;
import ru.otus.homework.api.model.Account;
import ru.otus.homework.api.model.User;
import ru.otus.homework.api.service.DbService;
import ru.otus.homework.api.service.DbServiceImpl;
import ru.otus.homework.jdbc.dao.ObjectDaoJdbc;
import ru.otus.homework.jdbc.sessionmanager.SessionManagerJdbc;

import javax.sql.DataSource;
import java.math.BigDecimal;
import java.sql.PreparedStatement;
import java.sql.SQLException;

import static org.junit.jupiter.api.Assertions.*;

class JdbcTemplateTest {
    private DbService dbService;
    private DataSource dataSource;
    private User user1 = new User(1, "name1", 20);
    private User user2 = new User(2, "name2", 30);
    private Account account1 = new Account(1l, "account1", new BigDecimal(111));
    private Account account2 = new Account(2l, "account2", new BigDecimal(333));

    @BeforeEach
    public void init() throws SQLException {
        dataSource = new DataSourceH2();
        SessionManagerJdbc sessionManager = new SessionManagerJdbc(dataSource);
        ObjectDao objectDao = new ObjectDaoJdbc(sessionManager);
        dbService = new DbServiceImpl(objectDao);
        createTables(dataSource);
    }

    @AfterEach
    public void tearDown() throws SQLException {
        dropTable(dataSource);
    }

    @Test
    void createAndLoadUsers() {
        dbService.saveObject(user1);
        dbService.saveObject(user2);

        User returnedUser1 = (User) dbService.getObject(1, User.class).get();
        User returnedUser2 = (User) dbService.getObject(2, User.class).get();

        assertEquals(user1, returnedUser1);
        assertEquals(user2, returnedUser2);
    }

    @Test
    void createAndLoadAccounts() {
        dbService.saveObject(account1);
        dbService.saveObject(account2);

        Account returnedAccount1 = (Account) dbService.getObject(1, Account.class).get();
        Account returnedAccount2 = (Account) dbService.getObject(2, Account.class).get();

        assertEquals(account1, returnedAccount1);
        assertEquals(account2, returnedAccount2);
    }

    @Test
    void createAndUpdateUser() {
        dbService.saveObject(user1);

        dbService.updateObject(new User(1, "updatedName", 100));

        User returnedUser1 = (User) dbService.getObject(1, User.class).get();
        assertEquals("updatedName", returnedUser1.getName());
        assertEquals(100, returnedUser1.getAge().intValue());
    }

    @Test
    void createAndUpdateAccount() {
        dbService.saveObject(account1);

        dbService.updateObject(new Account(1l, "updatedAccount", new BigDecimal(1000)));

        Account returnedAccount1 = (Account) dbService.getObject(1, Account.class).get();
        assertEquals("updatedAccount", returnedAccount1.getType());
        assertEquals(1000, returnedAccount1.getRest().intValue());
    }

    @Test
    void createOrUpdateUser() {
        dbService.saveObject(user1);
        dbService.createOrUpdate(new User(1, "name1", 40));

        User returnedUser1 = (User) dbService.getObject(1, User.class).get();
        assertEquals("name1", returnedUser1.getName());
        assertEquals(40, returnedUser1.getAge().intValue());

        dbService.createOrUpdate(new User(2, "name", 37));
        User returnedUser2 = (User) dbService.getObject(2, User.class).get();
        assertEquals("name", returnedUser2.getName());
        assertEquals(37, returnedUser2.getAge().intValue());
    }

    @Test
    void createOrUpdateAccount() {
        dbService.saveObject(account1);
        dbService.createOrUpdate(new Account(1l, "account1", new BigDecimal(222)));

        Account returnedAccount1 = (Account) dbService.getObject(1, Account.class).get();
        assertEquals("account1", returnedAccount1.getType());
        assertEquals(222, returnedAccount1.getRest().intValue());

        dbService.createOrUpdate(new Account(2l, "account3", new BigDecimal(444)));
        Account returnedAccount2 = (Account) dbService.getObject(2, Account.class).get();
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