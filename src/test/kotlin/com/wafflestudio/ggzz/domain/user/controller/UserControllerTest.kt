package com.wafflestudio.ggzz.domain.user.controller

import com.fasterxml.jackson.databind.ObjectMapper
import com.wafflestudio.ggzz.domain.user.dto.UserDto
import com.wafflestudio.ggzz.domain.user.service.UserService
import org.junit.jupiter.api.Test

import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.extension.ExtendWith
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.mock.mockito.MockBean
import org.springframework.http.MediaType
import org.springframework.restdocs.RestDocumentationContextProvider
import org.springframework.restdocs.RestDocumentationExtension
import org.springframework.restdocs.mockmvc.MockMvcRestDocumentation
import org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document
import org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.post
import org.springframework.restdocs.payload.PayloadDocumentation
import org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath
import org.springframework.restdocs.payload.PayloadDocumentation.requestFields
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.result.MockMvcResultMatchers
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.header
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.status
import org.springframework.test.web.servlet.setup.DefaultMockMvcBuilder
import org.springframework.test.web.servlet.setup.MockMvcBuilders
import org.springframework.transaction.annotation.Transactional
import org.springframework.web.context.WebApplicationContext

@ExtendWith(RestDocumentationExtension::class)
@WebMvcTest(UserController::class)
internal class UserControllerTest {

    @Autowired
    private lateinit var objectMapper: ObjectMapper

    @MockBean
    private lateinit var userService: UserService

    private lateinit var mockMvc: MockMvc

    @BeforeEach
    fun setUp(webApplicationContext: WebApplicationContext, restDocumentation: RestDocumentationContextProvider) {
        this.mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext)
            .apply<DefaultMockMvcBuilder>(MockMvcRestDocumentation.documentationConfiguration(restDocumentation))
            .build()
    }

    @Test
    fun signup() {
        // given
        val request = UserDto.SignUpRequest(
            username = "username",
            nickname = "nickname",
            password = "password"
        )

        // when
        val result = this.mockMvc.perform(
            post("/signup")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request))
        )

        // then
        result.andExpect(status().isOk)
            .andDo(
                document(
                    "signup/200",
                    requestFields(
                        fieldWithPath("username").description("로그인 아이디"),
                        fieldWithPath("nickname").description("편지에 보여질 닉네임"),
                        fieldWithPath("password").description("로그인 비밀번호")
                    )
                )
            )
    }

    @Test
    fun login() {
        // given
        val request = UserDto.LoginRequest(
            username = "username",
            password = "password"
        )

        // when
        val result = this.mockMvc.perform(
            post("/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request))
        )

        // then
        result.andExpect(status().isOk)
            .andDo(
                document(
                    "login/200",
                    requestFields(
                        fieldWithPath("username").description("로그인 아이디"),
                        fieldWithPath("password").description("로그인 비밀번호")
                    )
                )
            )
    }

    @Test
    fun logout() {
        // when
        val result = this.mockMvc.perform(
            post("/logout")
        )

        // then
        result.andExpect(status().isOk)
            .andExpect(
                header().string(
                    "Set-cookie",
                    "JSESSIONID=; Path=/; Max-Age=0; Expires=Thu, 1 Jan 1970 00:00:00 GMT; Secure; HttpOnly; SameSite=None"
                )
            )
            .andDo(
                document(
                    "logout/200",
                )
            )
    }
}
