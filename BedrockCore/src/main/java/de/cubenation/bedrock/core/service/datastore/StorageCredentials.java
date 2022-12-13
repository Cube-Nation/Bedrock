package de.cubenation.bedrock.core.service.datastore;

import java.util.Map;

public record StorageCredentials(
        String driver,
        String url,
        String username,
        String password,
        int maxPoolSize,
        int minIdleConnections,
        int keepAliveTime,
        int connectionTimeout,
        Map<String, String> properties
) {
}
