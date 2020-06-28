package ru.otus.homework.messagesystem;

import ru.otus.homework.ms.Message;
import ru.otus.homework.ms.MsClient;

public interface MessageSystem {

    void addDbClient(MsClient msClient);

    void addFeClient(MsClient msClient);

    void removeClient(String clientId);

    boolean newMessage(Message msg);

    void dispose() throws InterruptedException;

    void dispose(Runnable callback) throws InterruptedException;

    void start();

    int currentQueueSize();
}

