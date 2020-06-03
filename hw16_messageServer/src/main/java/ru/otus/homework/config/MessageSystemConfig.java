package ru.otus.homework.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.context.event.EventListener;
import ru.otus.homework.exception.DataHandlerException;
import ru.otus.homework.messagesystem.MessageSystem;
import ru.otus.homework.messagesystem.MessageSystemImpl;
import ru.otus.homework.ms.MsClientImpl;
import ru.otus.homework.socket.ListenServer;
import ru.otus.homework.socket.ListenServerImpl;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Configuration
public class MessageSystemConfig {
    @Value("${server.port}")
    private Integer serverPort;

    private final DbClientsProperties dbClientsProperties;
    private final FeClientsProperties feClientsProperties;

    public MessageSystemConfig(DbClientsProperties dbClientsProperties, FeClientsProperties feClientsProperties) {
        this.dbClientsProperties = dbClientsProperties;
        this.feClientsProperties = feClientsProperties;
    }

    @Bean
    public MessageSystem messageSystem() {
        return new MessageSystemImpl();
    }

    @Bean
    public ListenServer messageServer() {
        return new ListenServerImpl(serverPort, msg -> messageSystem().newMessage(msg));
    }

    @EventListener
    public void onApplicationEvent(ContextRefreshedEvent event) {
        convertMap(dbClientsProperties.getMap()).forEach(msClient -> messageSystem().addDbClient(msClient));
        convertMap(feClientsProperties.getMap()).forEach(msClient -> messageSystem().addFeClient(msClient));
        messageServer().start();
    }

    private List<MsClientImpl> convertMap(Map<String, String> map) {
        return map.entrySet().stream().map(entry -> {
            try {
                String[] address = entry.getValue().split(":");
                return new MsClientImpl(entry.getKey(), address[0], Integer.parseInt(address[1]));
            } catch (Exception e) {
                throw new DataHandlerException("clients initialization failed", e);
            }
        }).collect(Collectors.toList());
    }
}
