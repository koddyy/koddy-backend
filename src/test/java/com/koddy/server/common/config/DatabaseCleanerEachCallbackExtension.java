package com.koddy.server.common.config;

import com.koddy.server.common.utils.DatabaseCleaner;
import org.junit.jupiter.api.extension.AfterEachCallback;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.springframework.test.context.junit.jupiter.SpringExtension;

public class DatabaseCleanerEachCallbackExtension implements AfterEachCallback {
    @Override
    public void afterEach(final ExtensionContext context) {
        final DatabaseCleaner databaseCleaner = SpringExtension.getApplicationContext(context).getBean(DatabaseCleaner.class);
        databaseCleaner.cleanUpDatabase();
    }
}
