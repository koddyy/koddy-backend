package com.koddy.server.common.config;

import com.koddy.server.common.utils.RedisCleaner;
import org.junit.jupiter.api.extension.AfterEachCallback;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.springframework.test.context.junit.jupiter.SpringExtension;

public class RedisCleanerEachCallbackExtension implements AfterEachCallback {
    @Override
    public void afterEach(final ExtensionContext context) {
        final RedisCleaner databaseCleaner = SpringExtension.getApplicationContext(context).getBean(RedisCleaner.class);
        databaseCleaner.cleanUpRedis();
    }
}
