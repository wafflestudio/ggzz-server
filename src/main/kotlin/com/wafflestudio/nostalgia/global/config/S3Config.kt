package com.wafflestudio.nostalgia.global.config

import com.amazonaws.auth.InstanceProfileCredentialsProvider
import com.amazonaws.services.s3.AmazonS3
import com.amazonaws.services.s3.AmazonS3ClientBuilder
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
class S3Config {

    @Value("\${cloud.aws.region.static}")
    private val region: String = ""

    @Bean
    fun amazonS3Client(): AmazonS3 {
        return AmazonS3ClientBuilder
            .standard()
            .withRegion(region)
            .withCredentials(InstanceProfileCredentialsProvider.getInstance())
            .build()
    }
}