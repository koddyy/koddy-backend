package com.koddy.server.common;

import com.koddy.server.common.config.DatabaseCleanerEachCallbackExtension;
import com.koddy.server.common.config.MySqlTestContainersExtension;
import com.koddy.server.common.config.RedisTestContainersExtension;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;

@Tag("Integrate")
@SpringBootTest
@ExtendWith({
        DatabaseCleanerEachCallbackExtension.class,
        MySqlTestContainersExtension.class,
        RedisTestContainersExtension.class
})
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
public abstract class IntegrateTest {
}
