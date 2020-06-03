package ru.otus.homework.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.context.event.EventListener;
import ru.otus.homework.handler.UserDataRequestHandler;
import ru.otus.homework.ms.MessageType;
import ru.otus.homework.ms.MsClient;
import ru.otus.homework.ms.MsClientImpl;
import ru.otus.homework.ms.RequestHandler;
import ru.otus.homework.service.UserService;
import ru.otus.homework.socket.ListenServer;
import ru.otus.homework.socket.ListenServerImpl;

@Configuration
public class DatabaseConfig {
    @Value("${ms.client.name}")
    private String clientName;

    @Value("${ms.host}")
    private String msHost;

    @Value("${ms.port}")
    private Integer msPort;

    @Value("${server.port}")
    private Integer serverPort;

    private final UserService userService;

    public DatabaseConfig(UserService userService) {
        this.userService = userService;
    }

    @Bean
    public RequestHandler requestHandler() {
        return new UserDataRequestHandler(userService);
    }

    @Bean
    public MsClient databaseClient() {
        MsClient msClient = new MsClientImpl(clientName, msHost, msPort);
        msClient.addHandler(MessageType.USER_SAVE, requestHandler());
        msClient.addHandler(MessageType.USER_LIST, requestHandler());
        return msClient;
    }

    @Bean
    public ListenServer databaseServer() {
        return new ListenServerImpl(serverPort, msg -> databaseClient().handle(msg));
    }

    @EventListener
    public void onApplicationEvent(ContextRefreshedEvent event) {
        databaseServer().start();
    }
}
