package com.wafflestudio.ggzz.domain.letter.controller

import com.wafflestudio.ggzz.domain.letter.service.LetterService
import com.wafflestudio.ggzz.domain.user.dto.UserDto
import com.wafflestudio.ggzz.domain.user.model.User
import com.wafflestudio.ggzz.domain.user.repository.UserRepository
import com.wafflestudio.ggzz.domain.user.service.UserService
import com.wafflestudio.ggzz.global.config.FirebaseConfig
import org.junit.jupiter.api.Test
import org.mockito.ArgumentMatchers
import org.mockito.Mockito
import org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.*
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest
import org.springframework.boot.test.mock.mockito.MockBean
import org.springframework.http.MediaType
import org.springframework.security.core.Authentication
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.status

////@ExtendWith(RestDocumentationExtension::class)
@WebMvcTest(MyLetterController::class)
@AutoConfigureMockMvc
class MyLetterControllerTest @Autowired constructor(
    val mockMvc: MockMvc
) {
    @MockBean lateinit var letterService: LetterService
    @MockBean lateinit var userService: UserService
    @MockBean lateinit var userRepository: UserRepository
    @MockBean lateinit var firebaseConfig: FirebaseConfig

    @Test
    fun getMyLetters() {
        // given
        setupTestEnvironment() // Authentication 설정

        // when
        val result = this.mockMvc.perform(get("/api/v1/me/letters")
            .contentType(MediaType.APPLICATION_JSON)
            .header("Authorization", "Bearer test_token"))

        // then
        result.andExpect(status().isOk)
//                    .andDo(
//                document(
//                    "getMyLetters/200",
//                    getDocumentRequest(),
//                    getDocumentResponse(),
//                    responseFields(
//                        fieldWithPath("count").description("내 편지 개수"),
//                        fieldWithPath("data[].id").description("API에 사용되는 편지 ID"),
//                        fieldWithPath("data[].created_at").description("편지 생성일자"),
//                        fieldWithPath("data[].created_by").description("편지 생성자 닉네임"),
//                        fieldWithPath("data[].title").description("편지 제목"),
//                        fieldWithPath("data[].summary").description("편지 내용"),
//                        fieldWithPath("data[].longitude").description("경도"),
//                        fieldWithPath("data[].latitude").description("위도")
//                    )
//                )
//            )
        SecurityContextHolder.clearContext() // 인증 정보 제거
    }

    private fun setupTestEnvironment() {
        val signUpRequest = UserDto.SignUpRequest("test_username", "test_nickname", "test_password")
        val createdUser = User("test_firebase_id", "test_username", "test_nickname", "encoded_password")

        // userService.updateOrCreate() 메서드의 Mock 설정
        Mockito.`when`(userService.updateOrCreate(signUpRequest)).thenReturn(createdUser)

        // firebaseConfig.getIdByToken() 메서드의 Mock 설정
        Mockito.`when`(firebaseConfig.getIdByToken(ArgumentMatchers.anyString())).thenReturn("test_firebase_id")

        // 인증된 사용자 설정
        val authentication = Mockito.mock(Authentication::class.java)
        Mockito.`when`(authentication.isAuthenticated).thenReturn(true)
        Mockito.`when`(authentication.principal).thenReturn(
            org.springframework.security.core.userdetails.User(
                "test_username",
                "test_password",
                emptyList()
            )
        )

        // SecurityContextHolder에 인증 정보 설정
        SecurityContextHolder.getContext().authentication = authentication
    }
}
