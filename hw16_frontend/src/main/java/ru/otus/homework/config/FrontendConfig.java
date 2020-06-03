package ru.otus.homework.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.context.event.EventListener;
import org.springframework.integration.ip.tcp.TcpInboundGateway;
import org.springframework.integration.ip.tcp.connection.TcpNetServerConnectionFactory;
import ru.otus.homework.handler.UserDataResponseHandler;
import ru.otus.homework.ms.MessageType;
import ru.otus.homework.ms.RequestHandler;
import ru.otus.homework.service.FrontendService;
import ru.otus.homework.service.FrontendServiceImpl;
import ru.otus.homework.ms.MsClient;
import ru.otus.homework.ms.MsClientImpl;
import ru.otus.homework.socket.FrontServer;
import ru.otus.homework.socket.FrontServerImp;

@Configuration
public class FrontendConfig {
    @Value("${ms.client.name}")
    private String clientName;

    @Value("${ms.host}")
    private String msHost;

    @Value("${ms.port}")
    private Integer msPort;

    @Value("${tcp.server.port}")
    private Integer tcpServerPort;

    @Bean
    public MsClient frontendClient() {
        return new MsClientImpl(clientName, msHost, msPort);
    }

    @Bean
    public FrontendService frontendService() {
        return new FrontendServiceImpl(frontendClient());
    }

    @Bean
    public RequestHandler responseHandler() {
        return new UserDataResponseHandler(frontendService());
    }

    @Bean
    public FrontServer frontServer() {
        return new FrontServerImp(msg -> frontendClient().handle(msg));
    }

    @Bean
    public TcpNetServerConnectionFactory server() {
        return new TcpNetServerConnectionFactory(tcpServerPort);
    }

    @Bean
    public TcpInboundGateway inGate() {
        TcpInboundGateway inGate = new TcpInboundGateway();
        inGate.setConnectionFactory(server());
        inGate.setRequestChannelName("inChannel");
        return inGate;
    }

    @EventListener
    public void onApplicationEvent(ContextRefreshedEvent event) {
        frontendClient().addHandler(MessageType.USER_SAVE, responseHandler());
        frontendClient().addHandler(MessageType.USER_LIST, responseHandler());
    }
}
