package com.koddy.server.common;

import com.koddy.server.common.containers.MySqlTestContainers;
import com.koddy.server.global.config.etc.P6SpyConfig;
import com.koddy.server.global.config.infra.QueryDslConfig;
import org.junit.jupiter.api.Tag;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ContextConfiguration;

@Tag("Repository")
@DataJpaTest(showSql = false)
@ContextConfiguration(initializers = MySqlTestContainers.Initializer.class)
@Import({QueryDslConfig.class, P6SpyConfig.class})
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
public abstract class RepositoryTest {
}
