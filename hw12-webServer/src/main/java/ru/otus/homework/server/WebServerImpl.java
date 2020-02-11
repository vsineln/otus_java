package ru.otus.homework.server;

import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.handler.HandlerList;
import org.eclipse.jetty.server.handler.ResourceHandler;
import org.eclipse.jetty.servlet.FilterHolder;
import org.eclipse.jetty.servlet.ServletContextHandler;
import org.eclipse.jetty.servlet.ServletHolder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.otus.homework.dao.UserDao;
import ru.otus.homework.services.AuthenticateService;
import ru.otus.homework.servlet.ErrorHandler;
import ru.otus.homework.services.TemplateProcessor;
import ru.otus.homework.servlet.AdminServlet;
import ru.otus.homework.servlet.AuthorizationFilter;
import ru.otus.homework.servlet.LoginServlet;
import ru.otus.homework.servlet.LogoutServlet;

import java.util.stream.IntStream;

import static ru.otus.homework.util.Constants.ADMIN_PATH;
import static ru.otus.homework.util.Constants.LOGIN_PATH;
import static ru.otus.homework.util.Constants.LOGOUT_PATH;

public class WebServerImpl implements WebServer {
    private static final Logger log = LoggerFactory.getLogger(WebServerImpl.class);
    private final AuthenticateService authenticateService;
    private final TemplateProcessor templateProcessor;
    private final UserDao userDao;
    private final Server server;
    private final int port;

    public WebServerImpl(int port, AuthenticateService authenticateService, UserDao userDao, TemplateProcessor templateProcessor) {
        this.port = port;
        this.authenticateService = authenticateService;
        this.userDao = userDao;
        this.templateProcessor = templateProcessor;
        this.server = initContext();
    }

    @Override
    public void start() {
        try {
            server.start();
        } catch (Exception e) {
            throw new WebServerException(e.getMessage());
        }
    }

    @Override
    public void join() {
        try {
            server.join();
        } catch (InterruptedException e) {
            log.error(e.getMessage());
            Thread.currentThread().interrupt();
        }
    }

    @Override
    public void stop() {
        try {
            server.stop();
        } catch (Exception e) {
            throw new WebServerException(e.getMessage());
        }
    }

    private Server initContext() {
        HandlerList handlers = new HandlerList();
        handlers.addHandler(createResourceHandler());
        handlers.addHandler(applyFilterBasedSecurity(createServletContextHandler(), ADMIN_PATH));

        Server srv = new Server(port);
        srv.setHandler(handlers);
        return srv;
    }

    private ResourceHandler createResourceHandler() {
        ResourceHandler resourceHandler = new ResourceHandler();
        resourceHandler.setDirectoriesListed(false);
        return resourceHandler;
    }

    private ServletContextHandler createServletContextHandler() {
        ServletContextHandler servletContextHandler = new ServletContextHandler(ServletContextHandler.SESSIONS);
        servletContextHandler.addServlet(new ServletHolder(new AdminServlet(templateProcessor, userDao)), ADMIN_PATH);
        servletContextHandler.setErrorHandler(new ErrorHandler(templateProcessor));
        return servletContextHandler;
    }

    private ServletContextHandler applyFilterBasedSecurity(ServletContextHandler servletContextHandler, String... paths) {
        servletContextHandler.addServlet(new ServletHolder(new LoginServlet(templateProcessor, authenticateService)), LOGIN_PATH);
        servletContextHandler.addServlet(new ServletHolder(new LogoutServlet()), LOGOUT_PATH);
        AuthorizationFilter authorizationFilter = new AuthorizationFilter();
        IntStream.range(0, paths.length)
                .forEachOrdered(i -> servletContextHandler.addFilter(new FilterHolder(authorizationFilter), paths[i], null));
        return servletContextHandler;
    }
}
