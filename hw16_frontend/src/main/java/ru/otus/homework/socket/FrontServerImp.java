package ru.otus.homework.socket;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.integration.annotation.ServiceActivator;
import ru.otus.homework.ms.Message;

import java.io.ByteArrayInputStream;
import java.io.ObjectInputStream;
import java.util.function.Consumer;

public class FrontServerImp implements FrontServer {
    private static final Logger LOG = LoggerFactory.getLogger(FrontServerImp.class);

    private final Consumer<Message> consumer;

    public FrontServerImp(Consumer<Message> consumer) {
        this.consumer = consumer;
    }

    @ServiceActivator(inputChannel = "inChannel")
    public void listen(byte[] in) {
        LOG.info("front server received {} bytes", in.length);
        try {
            ObjectInputStream ois = new ObjectInputStream(new ByteArrayInputStream(in));
            Message msg = (Message) ois.readObject();
            LOG.info("handle message {}", msg.getId());
            consumer.accept(msg);
        } catch (Exception e) {
            LOG.error("error for front server", e);
        }
    }
}
