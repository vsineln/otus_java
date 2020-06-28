package ru.otus.homework.dto;

public enum Role {
    ADMIN("admin"),
    USER("user");

    private final String displayName;

    Role(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }
}
