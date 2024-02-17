package com.koddy.server.common;

import com.koddy.server.common.containers.MySqlTestContainers;
import com.koddy.server.common.containers.RedisTestContainers;
import com.koddy.server.common.containers.callback.DatabaseCleanerEachCallbackExtension;
import com.koddy.server.common.containers.callback.RedisCleanerEachCallbackExtension;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ContextConfiguration;

@Tag("Integrate")
@SpringBootTest
@ContextConfiguration(initializers = {
        MySqlTestContainers.Initializer.class,
        RedisTestContainers.Initializer.class
})
@ExtendWith({
        DatabaseCleanerEachCallbackExtension.class,
        RedisCleanerEachCallbackExtension.class
})
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
public abstract class IntegrateTest {
}
