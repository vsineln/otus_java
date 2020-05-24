package ru.otus.homework.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.context.event.EventListener;
import ru.otus.homework.front.FrontendService;
import ru.otus.homework.front.FrontendServiceImpl;
import ru.otus.homework.front.handlers.UserDataResponseHandler;
import ru.otus.homework.messagesystem.MessageSystem;
import ru.otus.homework.messagesystem.MessageType;
import ru.otus.homework.messagesystem.MsClient;
import ru.otus.homework.messagesystem.MsClientImpl;
import ru.otus.homework.messagesystem.RequestHandler;
import ru.otus.homework.service.UserService;
import ru.otus.homework.service.handlers.UserDataRequestHandler;

@Configuration
public class MessageSystemConfig {
    private static final String FRONTEND_CLIENT_NAME = "frontendService";
    private static final String DATABASE_CLIENT_NAME = "databaseService";

    private final MessageSystem messageSystem;
    private final UserService userService;

    public MessageSystemConfig(MessageSystem messageSystem, UserService userService) {
        this.messageSystem = messageSystem;
        this.userService = userService;
    }

    @Bean
    public RequestHandler requestHandler() {
        return new UserDataRequestHandler(userService);
    }

    @Bean
    public MsClient databaseClient() {
        MsClient msClient = new MsClientImpl(DATABASE_CLIENT_NAME, messageSystem);
        msClient.addHandler(MessageType.USER_SAVE, requestHandler());
        msClient.addHandler(MessageType.USER_LIST, requestHandler());
        return msClient;
    }

    @Bean
    MsClient frontendClient() {
        return new MsClientImpl(FRONTEND_CLIENT_NAME, messageSystem);
    }

    @Bean
    public FrontendService frontendService() {
        return new FrontendServiceImpl(frontendClient(), DATABASE_CLIENT_NAME);
    }

    @Bean
    public RequestHandler responseHandler() {
        return new UserDataResponseHandler(frontendService());
    }

    @EventListener
    public void onApplicationEvent(ContextRefreshedEvent event) {
        frontendClient().addHandler(MessageType.USER_SAVE, responseHandler());
        frontendClient().addHandler(MessageType.USER_LIST, responseHandler());

        messageSystem.addClient(frontendClient());
        messageSystem.addClient(databaseClient());
    }
}
