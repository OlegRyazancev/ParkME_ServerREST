package ru.ryazancev.testutils.paths;

import lombok.Getter;

@Getter
public enum PathParts {

    CARS("cars"),
    USERS("users"),
    RESERVATIONS("reservations"),
    ZONES("zones"),
    PLACES("places"),

    STATUS("status"),
    FREE("free");

    private final String value;

    PathParts(String value) {
        this.value = value;
    }
}
