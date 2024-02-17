package com.koddy.server.common.config

import com.koddy.server.global.annotation.UseCase
import org.junit.platform.commons.util.ClassFilter
import org.junit.platform.commons.util.ReflectionUtils
import org.mockito.Mockito
import org.springframework.beans.factory.config.BeanDefinition
import org.springframework.beans.factory.config.BeanFactoryPostProcessor
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory
import org.springframework.beans.factory.support.BeanDefinitionBuilder
import org.springframework.beans.factory.support.BeanDefinitionRegistry

class MockAllUseCaseBeanFactoryPostProcessor : BeanFactoryPostProcessor {
    override fun postProcessBeanFactory(beanFactory: ConfigurableListableBeanFactory) {
        val registry = beanFactory as BeanDefinitionRegistry
        val classFilter = ClassFilter.of { it.isAnnotationPresent(UseCase::class.java) }
        ReflectionUtils.findAllClassesInPackage("com.koddy.server", classFilter)
            .forEach {
                val bean: BeanDefinition = BeanDefinitionBuilder.genericBeanDefinition(it).beanDefinition
                registry.registerBeanDefinition(it.simpleName, bean)
                beanFactory.registerSingleton(it.simpleName, Mockito.mock(it))
            }
    }
}
