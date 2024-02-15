package com.koddy.server.common

import com.fasterxml.jackson.databind.ObjectMapper
import com.koddy.server.auth.domain.service.TokenProvider
import com.koddy.server.common.config.MockAllUseCaseBeanFactoryPostProcessor
import com.koddy.server.common.config.ResetMockTestExecutionListener
import com.koddy.server.common.config.TestAopConfig
import com.koddy.server.common.config.TestWebBeanConfig
import com.koddy.server.global.exception.alert.SlackAlertManager
import io.kotest.core.spec.style.BehaviorSpec
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Tag
import org.junit.jupiter.api.extension.ExtendWith
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.restdocs.AutoConfigureRestDocs
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest
import org.springframework.context.annotation.Import
import org.springframework.restdocs.RestDocumentationContextProvider
import org.springframework.restdocs.RestDocumentationExtension
import org.springframework.restdocs.mockmvc.MockMvcRestDocumentation
import org.springframework.test.context.TestExecutionListeners
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.result.MockMvcResultHandlers
import org.springframework.test.web.servlet.setup.DefaultMockMvcBuilder
import org.springframework.test.web.servlet.setup.MockMvcBuilders
import org.springframework.web.context.WebApplicationContext
import org.springframework.web.filter.CharacterEncodingFilter

@Tag("Controller")
@WebMvcTest
@ExtendWith(RestDocumentationExtension::class)
@Import(
    TestAopConfig::class,
    TestWebBeanConfig::class,
    MockAllUseCaseBeanFactoryPostProcessor::class,
)
@TestExecutionListeners(
    value = [ResetMockTestExecutionListener::class],
    mergeMode = TestExecutionListeners.MergeMode.MERGE_WITH_DEFAULTS,
)
@AutoConfigureRestDocs
abstract class ControllerTestKt : BehaviorSpec() {
    private lateinit var mockMvc: MockMvc

    @Autowired
    private lateinit var objectMapper: ObjectMapper

    @Autowired
    private lateinit var tokenProvider: TokenProvider

    @Autowired
    private lateinit var slackAlertManager: SlackAlertManager

    @BeforeEach
    fun setUp(context: WebApplicationContext, provider: RestDocumentationContextProvider) {
        this.mockMvc = MockMvcBuilders.webAppContextSetup(context)
            .apply { MockMvcRestDocumentation.documentationConfiguration(provider) }
            .alwaysDo<DefaultMockMvcBuilder> { MockMvcResultHandlers.print() }
            .alwaysDo<DefaultMockMvcBuilder> { MockMvcResultHandlers.log() }
            .addFilter<DefaultMockMvcBuilder>(CharacterEncodingFilter("UTF-8", true))
            .build()
    }
}
