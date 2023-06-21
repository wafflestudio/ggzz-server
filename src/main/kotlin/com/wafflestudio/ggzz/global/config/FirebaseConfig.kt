package com.wafflestudio.ggzz.global.config

import com.google.auth.oauth2.GoogleCredentials
import com.google.firebase.FirebaseApp
import com.google.firebase.FirebaseOptions
import com.google.firebase.auth.FirebaseAuth
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Service
import java.io.ByteArrayInputStream

interface FirebaseConfig {
    fun verifyId(id: String): String
    fun getIdByToken(token: String): String
}

@Service
class FirebaseConfigImpl(
    @Value("\${google-services.json}") private val googleServicesJsonString: String
) : FirebaseConfig {
    private val firebaseAuth: FirebaseAuth by lazy {
        try {
            val googleCredentials =
                GoogleCredentials.fromStream(ByteArrayInputStream(googleServicesJsonString.toByteArray()))

            val options = FirebaseOptions.builder()
                .setCredentials(googleCredentials)
                .build()

            FirebaseApp.initializeApp(options)
        } catch (e: Exception) {
            e.printStackTrace()
        }
        FirebaseAuth.getInstance()
    }

    override fun verifyId(id: String): String =
        firebaseAuth.getUser(id).uid

    override fun getIdByToken(token: String): String =
        firebaseAuth.verifyIdToken(token).uid
}
