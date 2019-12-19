package ru.otus.homework;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import ru.otus.homework.api.dao.ObjectDao;
import ru.otus.homework.api.model.AddressDataSet;
import ru.otus.homework.api.model.PhoneDataSet;
import ru.otus.homework.api.model.User;
import ru.otus.homework.api.service.DbService;
import ru.otus.homework.api.service.DbServiceImpl;
import ru.otus.homework.hibernate.dao.UserDaoHibernate;
import ru.otus.homework.hibernate.sessionmanager.SessionManagerHibernate;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.params.provider.Arguments.arguments;

@DisplayName("Data base service test with Hibernate")
class DbServiceTest {
    private static final String HIBERNATE_CFG_XML_FILE_RESOURCE = "hibernate-test.cfg.xml";
    private final List<PhoneDataSet> phones1 = Arrays.asList(new PhoneDataSet("1-1-3"), new PhoneDataSet("3-3-5"));
    private final List<PhoneDataSet> phones2 = Arrays.asList(new PhoneDataSet("7-7-7"), new PhoneDataSet("5-5-5"));
    private final AddressDataSet address1 = new AddressDataSet("MagicStreet");
    private final AddressDataSet address2 = new AddressDataSet("MagicForest");
    private static final String USER_NAME = "Wizard";

    private DbService dbService;
    private SessionFactory sessionFactory;

    @BeforeEach
    void init() {
        sessionFactory = HibernateUtils.buildSessionFactory(HIBERNATE_CFG_XML_FILE_RESOURCE, User.class, AddressDataSet.class, PhoneDataSet.class);
        SessionManagerHibernate sessionManager = new SessionManagerHibernate(sessionFactory);
        ObjectDao userDao = new UserDaoHibernate(sessionManager);
        dbService = new DbServiceImpl(userDao);
    }

    @AfterEach
    void tearDown() {
        sessionFactory.close();
    }

    @Test
    @DisplayName("Tests that user can be saved")
    void saveUser() {
        User user = createUser(address1, phones1);

        long id = dbService.saveObject(user);

        User loadedUser = loadUser(id);
        compareUsers(user, loadedUser);
    }

    @Test
    @DisplayName("Tests that user can be loaded")
    void getUser() {
        User user = createUser(address1, phones1);
        saveUser(user);

        Optional<Object> returnedUser = dbService.getObject(user.getId(), User.class);

        assertThat(returnedUser.isPresent());
        compareUsers(user, (User) returnedUser.get());
    }

    @ParameterizedTest(name = "Tests that user can be updated: {2}")
    @MethodSource("updateAddressAndPhones")
    void updateUser(boolean changeAddress, boolean changePhones, String doc) {
        User user = createUser(address1, phones1);
        saveUser(user);
        User updatedUser = createUser(changeAddress ? address2 : address1, changePhones ? phones2 : phones1);
        updatedUser.setId(user.getId());

        dbService.updateObject(updatedUser);

        User loadedUser = loadUser(user.getId());
        compareUsers(updatedUser, loadedUser);
    }

    @Test
    @DisplayName("Tests that user can be created or updated")
    void createOrUpdateUser() {
        User user = createUser(address1, phones1);
        long id = dbService.createOrUpdate(user);
        User loadedUser = loadUser(id);
        compareUsers(user, loadedUser);

        User updatedUser = createUser(address2, phones2);
        updatedUser.setId(id);
        dbService.createOrUpdate(updatedUser);
        loadedUser = loadUser(id);
        compareUsers(updatedUser, loadedUser);
    }

    private User createUser(AddressDataSet address, List<PhoneDataSet> phones) {
        return new User(USER_NAME, 101, address, phones);
    }

    private User loadUser(long id) {
        try (Session session = sessionFactory.openSession()) {
            return session.find(User.class, id);
        }
    }

    private void saveUser(User user) {
        try (Session session = sessionFactory.openSession()) {
            session.beginTransaction();
            session.save(user);
            session.getTransaction().commit();
        }
    }

    private void compareUsers(User user, User loadedUser) {
        assertEquals(user.getName(), loadedUser.getName());
        assertEquals(user.getAge(), loadedUser.getAge());
        assertEquals(user.getAddress().getStreet(), loadedUser.getAddress().getStreet());
        assertArrayEquals(user.getPhones().toArray(), loadedUser.getPhones().toArray());
    }

    static Stream<Arguments> updateAddressAndPhones() {
        return Stream.of(
                arguments(true, false, "update address"),
                arguments(false, true, "update phones"),
                arguments(true, true, "update address and phones")
        );
    }
}
