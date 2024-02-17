package com.koddy.server.common;

import com.koddy.server.common.containers.RedisTestContainers;
import org.junit.jupiter.api.Tag;
import org.springframework.boot.test.autoconfigure.data.redis.DataRedisTest;
import org.springframework.test.context.ContextConfiguration;

@Tag("Redis")
@DataRedisTest
@ContextConfiguration(initializers = RedisTestContainers.Initializer.class)
public abstract class RedisTest {
}
