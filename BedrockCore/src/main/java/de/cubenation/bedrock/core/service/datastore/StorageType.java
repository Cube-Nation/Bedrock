package de.cubenation.bedrock.core.service.datastore;

import lombok.Getter;

import java.util.regex.Pattern;

public enum StorageType {
    MYSQL(null),
    MARIADB(null);

    @Getter
    private final Pattern pattern;

    StorageType(Pattern regex) {
        if (regex == null) {
            regex = Pattern.compile("^jdbc:([a-z]+):", Pattern.CASE_INSENSITIVE);
        }
        this.pattern = regex;
    }
}
