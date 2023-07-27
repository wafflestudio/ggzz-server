package com.wafflestudio.ggzz.global.config

import org.springframework.beans.factory.config.BeanFactoryPostProcessor
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory
import org.springframework.boot.json.JacksonJsonParser
import org.springframework.context.EnvironmentAware
import org.springframework.context.annotation.Configuration
import org.springframework.core.env.Environment
import software.amazon.awssdk.regions.Region
import software.amazon.awssdk.services.secretsmanager.SecretsManagerClient
import software.amazon.awssdk.services.secretsmanager.model.GetSecretValueRequest

@Configuration
class SecretsManagerConfig : EnvironmentAware, BeanFactoryPostProcessor {

    private val jsonParser = JacksonJsonParser()
    private lateinit var env: Environment

    override fun setEnvironment(environment: Environment) {
        env = environment
    }

    override fun postProcessBeanFactory(beanFactory: ConfigurableListableBeanFactory) {
        env.getProperty("secrets-manager")?.split(",")?.forEach { secretName ->
            val secretString = getSecretString(secretName, Region.AP_NORTHEAST_2)
            jsonParser.parseMap(secretString).forEach { (key, value) ->
                System.setProperty(key, value.toString())
            }
        }
    }

    fun getSecretString(secretName: String, region: Region): String {
        val client = SecretsManagerClient.builder().region(region).build()
        val request = GetSecretValueRequest.builder().secretId(secretName).build()
        return client.getSecretValue(request).secretString()
    }
}
