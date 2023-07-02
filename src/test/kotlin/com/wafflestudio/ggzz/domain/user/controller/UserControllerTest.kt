package com.wafflestudio.ggzz.domain.user.controller

import com.wafflestudio.ggzz.domain.user.dto.UserDto
import com.wafflestudio.ggzz.domain.user.model.User
import com.wafflestudio.ggzz.domain.user.repository.UserRepository
import com.wafflestudio.ggzz.domain.user.service.UserService
import com.wafflestudio.ggzz.global.config.FirebaseConfig
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.Mockito.*
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest
import org.springframework.boot.test.mock.mockito.MockBean
import org.springframework.http.MediaType
import org.springframework.security.core.Authentication
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf
import org.springframework.test.context.junit.jupiter.SpringExtension
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.*
import java.util.*
import org.springframework.security.core.userdetails.User as SecurityUser

@ExtendWith(SpringExtension::class)
@WebMvcTest(UserController::class)
@AutoConfigureMockMvc
class UserControllerTests @Autowired constructor(
    val mockMvc: MockMvc
) {
    @MockBean
    lateinit var userService: UserService

    @MockBean
    lateinit var userRepository: UserRepository

    @MockBean
    lateinit var firebaseConfig: FirebaseConfig

    @Test
    fun signupUserTest() {
        // Mock 데이터
        val test_token = "test_token"
        val signUpRequest = UserDto.SignUpRequest("test_username", "test_nickname", "test_password")
        val createdUser = User("test_firebase_id", "test_username", "test_nickname", "encoded_password")

        // userService.updateOrCreate() 메서드의 Mock 설정
        `when`(userService.updateOrCreate(signUpRequest)).thenReturn(createdUser)

        // firebaseConfig.getIdByToken() 메서드의 Mock 설정
        `when`(firebaseConfig.getIdByToken(anyString())).thenReturn("test_firebase_id")

        // 인증된 사용자 설정
        val authentication = mock(Authentication::class.java)
        `when`(authentication.isAuthenticated).thenReturn(true)
        `when`(authentication.principal).thenReturn(SecurityUser("test_username", "test_password", emptyList()))

        // SecurityContextHolder에 인증 정보 설정
        SecurityContextHolder.getContext().authentication = authentication

        // POST 요청을 수행하고 응답을 검증
        mockMvc.perform(
            post("/signup")
                .contentType(MediaType.APPLICATION_JSON)
                .content("""{"username":"test_username", "nickname":"test_nickname", "password":"test_password"}""")
                .header("Authorization", "Bearer $test_token")
                .with(csrf())
        )
            .andExpect(status().isOk)
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
            .andExpect(jsonPath("$.username").value("test_username"))
            .andExpect(jsonPath("$.nickname").value("test_nickname"))

        // userService.updateOrCreate() 메서드가 올바르게 호출되었는지 검증
        verify(userService, times(1)).updateOrCreate(signUpRequest)

        // firebaseConfig.getIdByToken() 메서드가 올바르게 호출되었는지 검증
        verify(firebaseConfig, times(1)).getIdByToken(test_token)

        // SecurityContextHolder의 인증 정보 제거
        SecurityContextHolder.clearContext()
    }
}
