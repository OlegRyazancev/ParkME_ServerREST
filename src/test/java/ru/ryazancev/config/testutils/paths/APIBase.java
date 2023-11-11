package ru.ryazancev.config.testutils.paths;

import lombok.Getter;

@Getter
public enum APIBase {
    V1("/api/v1");

    private final String value;

    APIBase(String value) {
        this.value = value;
    }

}
