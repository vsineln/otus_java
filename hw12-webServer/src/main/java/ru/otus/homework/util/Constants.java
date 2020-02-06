package ru.otus.homework.util;

public class Constants {
    public static final String LOGIN_PARAMETER = "login";
    public static final String PASSWORD_PARAMETER = "password";
    public static final String NAME_PARAMETER = "name";
    public static final String ROLE_PARAMETER = "role";

    public static final String LOGIN_PATH = "/login";
    public static final String LOGOUT_PATH = "/logout";
    public static final String ADMIN_PATH = "/admin";

    public static final String LOGIN_PAGE = "login.html";
    public static final String ADMIN_PAGE = "admin.html";
    public static final String TXT_HTML = "text/html";
    public static final String ADMIN = "admin";
    public static final String USERS = "users";

    public static final String TEMPLATES_DIR = "/templates/";
    public static final String PROPERTIES_FILE = "application.properties";

    public static final String DB_HOST = "db.host";
    public static final String DB_PORT = "db.port";
    public static final String DB_NAME = "db.name";
    public static final String SERVER_PORT = "server.port";


    public static final int LOG_ROUNDS = 11;
    public static final int MAX_INACTIVE_INTERVAL = 30;


    private Constants() {
    }
}
