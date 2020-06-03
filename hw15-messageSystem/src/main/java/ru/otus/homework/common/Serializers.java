package ru.otus.homework.common;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.otus.homework.exception.UserSerializationException;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

public final class Serializers {
    private static final Logger LOG = LoggerFactory.getLogger(Serializers.class);

    private Serializers() {
    }

    public static byte[] serialize(Object data) {
        try (ByteArrayOutputStream baos = new ByteArrayOutputStream();
             ObjectOutputStream os = new ObjectOutputStream(baos)) {
            os.writeObject(data);
            os.flush();
            return baos.toByteArray();
        } catch (Exception e) {
            LOG.error("Serialization error, data: {}", data);
            throw new UserSerializationException("Serialization error", e.getCause());
        }
    }

    public static <T> T deserialize(byte[] data, Class<T> classOfT) {
        try (ByteArrayInputStream bis = new ByteArrayInputStream(data);
             ObjectInputStream is = new ObjectInputStream(bis)) {
            Object obj = is.readObject();
            return classOfT.cast(obj);
        } catch (Exception e) {
            LOG.error("DeSerialization error, classOfT: {}", classOfT.getName());
            throw new UserSerializationException("DeSerialization error", e.getCause());
        }
    }
}
