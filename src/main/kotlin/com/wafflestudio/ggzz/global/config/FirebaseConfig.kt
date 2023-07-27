package com.wafflestudio.ggzz.global.config

import com.google.auth.oauth2.GoogleCredentials
import com.google.firebase.FirebaseApp
import com.google.firebase.FirebaseOptions
import com.google.firebase.auth.FirebaseAuth
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import java.io.ByteArrayInputStream

@Configuration
class FirebaseConfig {

    @Bean
    fun firebaseAuth(@Value("\${google-services.json}") googleServicesJsonString: String): FirebaseAuth {
        try {
            val googleCredentials =
                GoogleCredentials.fromStream(ByteArrayInputStream(googleServicesJsonString.toByteArray()))

            val options = FirebaseOptions.builder()
                .setCredentials(googleCredentials)
                .build()

            FirebaseApp.initializeApp(options)
            return FirebaseAuth.getInstance()
        } catch (e: Exception) {
            e.printStackTrace()
            throw IllegalStateException("Failed to initialize FirebaseAuth.")
        }
    }

}
