package com.koddy.server.common.config

import org.mockito.internal.util.MockUtil
import org.springframework.context.ApplicationContext
import org.springframework.test.context.TestContext
import org.springframework.test.context.TestExecutionListener

class ResetMockTestExecutionListener : TestExecutionListener {
    override fun afterTestMethod(testContext: TestContext) {
        val applicationContext: ApplicationContext = testContext.applicationContext
        if (mockCache.isEmpty() || isNewContext(applicationContext)) {
            mockCache.clear()
            applicationContextStartupDate = applicationContext.startupDate
            initMocks(applicationContext)
        }
        mockCache.forEach { MockUtil.resetMock(it) }
    }

    private fun isNewContext(applicationContext: ApplicationContext): Boolean = applicationContext.startupDate != applicationContextStartupDate

    private fun initMocks(applicationContext: ApplicationContext) =
        applicationContext.beanDefinitionNames
            .map { applicationContext.getBean(it) }
            .filter { MockUtil.isMock(it) }
            .forEach { mockCache.add(it) }

    companion object {
        private var applicationContextStartupDate: Long = 0
        private val mockCache: MutableList<Any> = ArrayList()
    }
}
