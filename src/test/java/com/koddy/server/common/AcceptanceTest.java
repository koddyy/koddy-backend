package com.koddy.server.common;

import com.koddy.server.auth.infrastructure.oauth.google.GoogleOAuthConnector;
import com.koddy.server.auth.infrastructure.oauth.google.GoogleOAuthUriGenerator;
import com.koddy.server.auth.infrastructure.oauth.kakao.KakaoOAuthConnector;
import com.koddy.server.auth.infrastructure.oauth.kakao.KakaoOAuthUriGenerator;
import com.koddy.server.auth.infrastructure.oauth.zoom.ZoomOAuthConnector;
import com.koddy.server.auth.infrastructure.oauth.zoom.ZoomOAuthUriGenerator;
import com.koddy.server.coffeechat.infrastructure.link.zoom.ZoomMeetingLinkManager;
import com.koddy.server.common.config.BlackboxLogicControlConfig;
import com.koddy.server.common.config.ExternalApiConfig;
import com.koddy.server.common.containers.LocalStackTestContainersConfig;
import com.koddy.server.common.containers.MySqlTestContainersExtension;
import com.koddy.server.common.containers.RedisTestContainersExtension;
import io.restassured.RestAssured;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.context.annotation.Import;

@Tag("Acceptance")
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT, classes = LocalStackTestContainersConfig.class)
@ExtendWith({
        MySqlTestContainersExtension.class,
        RedisTestContainersExtension.class
})
@Import({ExternalApiConfig.class, BlackboxLogicControlConfig.class})
public abstract class AcceptanceTest {
    @LocalServerPort
    private int port;

    @MockBean
    private GoogleOAuthUriGenerator googleOAuthUriMock;

    @MockBean
    private KakaoOAuthUriGenerator kakaoOAuthUriMock;

    @MockBean
    private ZoomOAuthUriGenerator zoomOAuthUriMock;

    @MockBean
    private GoogleOAuthConnector googleOAuthConnectorMock;

    @MockBean
    private KakaoOAuthConnector kakaoOAuthConnectorMock;

    @MockBean
    private ZoomOAuthConnector zoomOAuthConnectorMock;

    @MockBean
    private ZoomMeetingLinkManager zoomMeetingLinkManager;

    @BeforeEach
    void setUp() {
        RestAssured.port = port;
    }
}
