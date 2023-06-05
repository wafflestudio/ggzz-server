import com.wafflestudio.ggzz.domain.user.controller.UserController
import com.wafflestudio.ggzz.domain.user.dto.UserDto
import com.wafflestudio.ggzz.domain.user.model.User
import com.wafflestudio.ggzz.domain.user.service.UserService
import com.wafflestudio.ggzz.global.config.FirebaseConfig
import org.junit.jupiter.api.Test
import org.mockito.Mockito.*
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest
import org.springframework.boot.test.mock.mockito.MockBean
import org.springframework.http.MediaType
import org.springframework.security.core.Authentication
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.*
import java.util.*
import org.springframework.security.core.userdetails.User as SecurityUser

@WebMvcTest(UserController::class)
class UserControllerTests {
    @Autowired
    private lateinit var mockMvc: MockMvc
    @MockBean
    private lateinit var userService: UserService
    @MockBean
    private lateinit var firebaseConfig: FirebaseConfig

    @Test
    fun `signup should return the created user`() {
        // Mock 데이터
        val request = UserDto.SignUpRequest("test_username", "test_nickname", "test_password")
        val createdUser = User("test_firebase_id", "test_username", "test_nickname", "encoded_password")

        // userService.updateOrCreate() 메서드의 Mock 설정
        `when`(userService.updateOrCreate(request)).thenReturn(createdUser)

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
                .header("Authorization", "Bearer test_token")
        )
            .andExpect(status().isOk)
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
            .andExpect(jsonPath("$.username").value("test_username"))
            .andExpect(jsonPath("$.nickname").value("test_nickname"))

        // userService.updateOrCreate() 메서드가 올바르게 호출되었는지 검증
        verify(userService, times(1)).updateOrCreate(request)

        // firebaseConfig.getIdByToken() 메서드가 올바르게 호출되었는지 검증
        verify(firebaseConfig, times(1)).getIdByToken("test_token")

        // SecurityContextHolder의 인증 정보 제거
        SecurityContextHolder.clearContext()
    }
}


//package com.wafflestudio.ggzz.domain.user.controller
//
//import com.fasterxml.jackson.databind.ObjectMapper
//import com.google.firebase.auth.FirebaseAuth
//import com.wafflestudio.ggzz.domain.user.dto.UserDto
//import com.wafflestudio.ggzz.global.config.FirebaseConfig
//import org.junit.jupiter.api.Test
//
//import org.junit.jupiter.api.Assertions.*
//import org.junit.jupiter.api.BeforeEach
//import org.junit.jupiter.api.extension.ExtendWith
//import org.springframework.beans.factory.annotation.Autowired
//import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest
//import org.springframework.boot.test.mock.mockito.MockBean
//import org.springframework.http.MediaType
//import org.springframework.restdocs.RestDocumentationContextProvider
//import org.springframework.restdocs.RestDocumentationExtension
//import org.springframework.restdocs.mockmvc.MockMvcRestDocumentation
//import org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document
//import org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.post
//import org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath
//import org.springframework.restdocs.payload.PayloadDocumentation.requestFields
//import org.springframework.test.web.servlet.MockMvc
//import org.springframework.test.web.servlet.result.MockMvcResultMatchers.header
//import org.springframework.test.web.servlet.result.MockMvcResultMatchers.status
//import org.springframework.test.web.servlet.setup.DefaultMockMvcBuilder
//import org.springframework.test.web.servlet.setup.MockMvcBuilders
//import org.springframework.web.context.WebApplicationContext
//
//@ExtendWith(RestDocumentationExtension::class)
//@WebMvcTest(UserController::class)
//internal class UserControllerTest {
//
//    @Autowired
//    private lateinit var objectMapper: ObjectMapper
//    private lateinit var mockMvc: MockMvc
//
//    @BeforeEach
//    fun setUp(webApplicationContext: WebApplicationContext, restDocumentation: RestDocumentationContextProvider) {
//        this.mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext)
//            .apply<DefaultMockMvcBuilder>(MockMvcRestDocumentation.documentationConfiguration(restDocumentation))
//            .build()
//    }
//
//    @Test
//    fun signup() {
//        // given
//        val request = UserDto.SignUpRequest(
//            username = "username",
//            nickname = "nickname",
//            password = "password"
//        )
//        val firebaseToken = "Test Firebase Token"
//
//        // when
//        val result = this.mockMvc.perform(
//            post("/signup")
//                .contentType(MediaType.APPLICATION_JSON)
//                .content(objectMapper.writeValueAsString(request))
//                .header("Authorization", "Bearer $firebaseToken")
//        )
//
//        // then
//        result.andExpect(status().isOk)
//            .andDo(
//                document(
//                    "signup/200",
//                    requestFields(
//                        fieldWithPath("username").description("로그인 아이디"),
//                        fieldWithPath("nickname").description("편지에 보여질 닉네임"),
//                        fieldWithPath("password").description("로그인 비밀번호")
//                    )
//                )
//            )
//    }
//
//    @Test
//    fun login() {
//        // given
//        val request = UserDto.LoginRequest(
//            username = "username",
//            password = "password"
//        )
//
//        // when
//        val result = this.mockMvc.perform(
//            post("/login")
//                .contentType(MediaType.APPLICATION_JSON)
//                .content(objectMapper.writeValueAsString(request))
//        )
//
//        // then
//        result.andExpect(status().isOk)
//            .andDo(
//                document(
//                    "login/200",
//                    requestFields(
//                        fieldWithPath("username").description("로그인 아이디"),
//                        fieldWithPath("password").description("로그인 비밀번호")
//                    )
//                )
//            )
//    }
//
//    @Test
//    fun logout() {
//        // when
//        val result = this.mockMvc.perform(
//            post("/logout")
//        )
//
//        // then
//        result.andExpect(status().isOk)
//            .andExpect(
//                header().string(
//                    "Set-cookie",
//                    "JSESSIONID=; Path=/; Max-Age=0; Expires=Thu, 1 Jan 1970 00:00:00 GMT; Secure; HttpOnly; SameSite=None"
//                )
//            )
//            .andDo(
//                document(
//                    "logout/200",
//                )
//            )
//    }
//}
