package ru.otus.homework.socket;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.otus.homework.ms.Message;

import java.io.ObjectInputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.function.Consumer;

public class ListenServerImpl implements ListenServer {
    private static final Logger LOG = LoggerFactory.getLogger(ListenServerImpl.class);

    private final int port;
    private final Consumer<Message> consumer;

    public ListenServerImpl(int port, Consumer<Message> consumer) {
        this.port = port;
        this.consumer = consumer;
    }

    @Override
    public void start() {
        try (ServerSocket serverSocket = new ServerSocket(port)) {
            while (!Thread.currentThread().isInterrupted()) {
                LOG.info("waiting for client connection");
                try (Socket clientSocket = serverSocket.accept()) {
                    clientHandler(clientSocket);
                }
            }
        } catch (Exception ex) {
            LOG.error("error", ex);
        }
    }

    private void clientHandler(Socket clientSocket) {
        try (ObjectInputStream in = new ObjectInputStream(clientSocket.getInputStream())) {
            Message msg = (Message) in.readObject();
            if (msg != null) {
                LOG.info("message from client: {}", msg);
                consumer.accept(msg);
            }
        } catch (Exception ex) {
            LOG.error("error", ex);
        }
    }
}
