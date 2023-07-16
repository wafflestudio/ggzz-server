package com.wafflestudio.ggzz.global.config

import com.fasterxml.jackson.databind.ObjectMapper
import org.springframework.beans.factory.config.BeanFactoryPostProcessor
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory
import org.springframework.boot.json.JacksonJsonParser
import org.springframework.context.EnvironmentAware
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Profile
import org.springframework.core.env.Environment
import software.amazon.awssdk.regions.Region
import software.amazon.awssdk.services.secretsmanager.SecretsManagerClient
import software.amazon.awssdk.services.secretsmanager.model.GetSecretValueRequest

@Configuration
@Profile("dev", "local", "test")
class SecretsManagerConfig(
    private val objectMapper: ObjectMapper
) : EnvironmentAware, BeanFactoryPostProcessor {
    private lateinit var env: Environment

    override fun setEnvironment(environment: Environment) {
        env = environment
    }

    override fun postProcessBeanFactory(beanFactory: ConfigurableListableBeanFactory) {
        val secretNames = env.getProperty("secrets-manager", "").split(",")
        val region = Region.AP_NORTHEAST_2
        val objectMapper = JacksonJsonParser(objectMapper)
        secretNames.forEach { secretName ->
            val secretString = getSecretString(secretName, region)
            val map = objectMapper.parseMap(secretString)
            map.forEach { (key, value) -> System.setProperty(key, value.toString()) }
        }
    }

    fun getSecretString(secretName: String, region: Region): String {
        val client = SecretsManagerClient.builder().region(region).build()
        val request = GetSecretValueRequest.builder().secretId(secretName).build()
        return client.getSecretValue(request).secretString()
    }
}
