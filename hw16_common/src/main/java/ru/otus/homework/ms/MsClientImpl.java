package ru.otus.homework.ms;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.otus.homework.common.Serializers;

import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;

public class MsClientImpl implements MsClient {
    private static final Logger LOG = LoggerFactory.getLogger(MsClientImpl.class);
    private final String name;
    private final String host;
    private final int port;
    private final Map<String, RequestHandler> handlers = new ConcurrentHashMap<>();

    public MsClientImpl(String name, String host, int port) {
        this.name = name;
        this.host = host;
        this.port = port;
    }

    @Override
    public void addHandler(MessageType type, RequestHandler requestHandler) {
        this.handlers.put(type.getValue(), requestHandler);
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public boolean sendMessage(Message msg) {
        LOG.info("send message {} ", msg);
        LOG.info("send to {} {}", host, port);
        try (Socket clientSocket = new Socket(host, port)) {
            ObjectOutputStream out = new ObjectOutputStream(clientSocket.getOutputStream());
            out.writeObject(msg);
            out.writeObject("\r\n");
        } catch (Exception e) {
            LOG.error("error, message can not be sent", e);
            return false;
        }
        return true;
    }

    @Override
    public void handle(Message msg) {
        LOG.info("handle message:{}", msg);
        try {
            RequestHandler requestHandler = handlers.get(msg.getType());
            if (requestHandler != null) {
                requestHandler.handle(msg).ifPresent(this::sendMessage);
            } else {
                LOG.error("handler not found for the message type:{}", msg.getType());
            }
        } catch (Exception ex) {
            LOG.error("msg: {}", msg, ex);
        }
    }

    @Override
    public <T> Message produceMessage(String to, T data, MessageType msgType) {
        return new Message(name, to, null, msgType.getValue(), Serializers.serialize(data));
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        MsClientImpl msClient = (MsClientImpl) o;
        return Objects.equals(name, msClient.name);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name);
    }
}
