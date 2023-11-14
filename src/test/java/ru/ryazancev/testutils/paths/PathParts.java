package ru.ryazancev.testutils.paths;

import lombok.Getter;

@Getter
public enum PathParts {
    AUTH("auth"),
    CARS("cars"),
    USERS("users"),
    RESERVATIONS("reservations"),
    ZONES("zones"),
    PLACES("places"),

    LOGIN("login"),
    REGISTER("register"),
    REFRESH("refresh"),


    STATUS("status"),
    FREE("free");


    private final String value;

    PathParts(String value) {
        this.value = value;
    }
}
