package ru.otus.homework.messagesystem;

public enum MessageType {
    USER_SAVE("UserSave"),
    USER_LIST("UserList");

    private final String value;

    MessageType(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }
}
