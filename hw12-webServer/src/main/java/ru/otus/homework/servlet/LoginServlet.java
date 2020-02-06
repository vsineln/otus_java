package ru.otus.homework.servlet;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.otus.homework.server.WebServerException;
import ru.otus.homework.services.AuthenticateService;
import ru.otus.homework.services.TemplateProcessor;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.util.Collections;

import static javax.servlet.http.HttpServletResponse.SC_UNAUTHORIZED;
import static ru.otus.homework.util.Constants.*;

public class LoginServlet extends HttpServlet {
    private static final Logger log = LoggerFactory.getLogger(LoginServlet.class);
    private final AuthenticateService authenticateService;
    private final TemplateProcessor templateProcessor;

    public LoginServlet(TemplateProcessor templateProcessor, AuthenticateService authenticateService) {
        this.templateProcessor = templateProcessor;
        this.authenticateService = authenticateService;
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        resp.setContentType(TXT_HTML);
        resp.getWriter().println(templateProcessor.getPage(LOGIN_PAGE, Collections.emptyMap()));
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        try {
            authenticateService.authenticateAdmin(req.getParameter(LOGIN_PARAMETER), req.getParameter(PASSWORD_PARAMETER));
            HttpSession httpSession = req.getSession();
            httpSession.setMaxInactiveInterval(MAX_INACTIVE_INTERVAL);
            resp.sendRedirect(ADMIN_PATH);
        } catch (WebServerException e) {
            log.error("Wrong login: {}", e.getMessage());
            resp.sendError(SC_UNAUTHORIZED, e.getMessage());
        }
    }
}
