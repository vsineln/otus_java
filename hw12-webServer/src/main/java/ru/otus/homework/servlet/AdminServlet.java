package ru.otus.homework.servlet;

import org.mindrot.jbcrypt.BCrypt;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.otus.homework.dao.UserDao;
import ru.otus.homework.dao.UserDaoException;
import ru.otus.homework.model.Role;
import ru.otus.homework.model.User;
import ru.otus.homework.services.TemplateProcessor;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import static javax.servlet.http.HttpServletResponse.SC_NOT_ACCEPTABLE;
import static ru.otus.homework.util.Constants.*;

public class AdminServlet extends HttpServlet {
    private static final Logger log = LoggerFactory.getLogger(AdminServlet.class);
    private final TemplateProcessor templateProcessor;
    private final UserDao userDao;

    public AdminServlet(TemplateProcessor templateProcessor, UserDao userDao) {
        this.templateProcessor = templateProcessor;
        this.userDao = userDao;
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        Map<String, Object> paramsMap = new HashMap<>();
        paramsMap.put(USERS, userDao.getUsers());
        resp.setContentType(TXT_HTML);
        resp.getWriter().println(templateProcessor.getPage(ADMIN_PAGE, paramsMap));
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        String name = req.getParameter(NAME_PARAMETER);
        String login = req.getParameter(LOGIN_PARAMETER);
        String password = BCrypt.hashpw(req.getParameter(PASSWORD_PARAMETER), BCrypt.gensalt(LOG_ROUNDS));
        String role = req.getParameter(ROLE_PARAMETER);

        try {
            userDao.createUser(new User(name, login, password, Role.valueOf(role.toUpperCase())));
            resp.sendRedirect(ADMIN_PATH);
        } catch (UserDaoException e) {
            log.error(e.getMessage());
            resp.sendError(SC_NOT_ACCEPTABLE, e.getMessage());
        }
    }
}
