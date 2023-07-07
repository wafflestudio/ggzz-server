package com.wafflestudio.ggzz.domain.user.controller

import com.fasterxml.jackson.databind.ObjectMapper
import com.wafflestudio.ggzz.domain.WithCustomUser
import com.wafflestudio.ggzz.domain.user.dto.UserDto
import com.wafflestudio.ggzz.domain.user.service.UserService
import jakarta.servlet.http.Cookie
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.ArgumentMatchers.anyLong
import org.mockito.BDDMockito.given
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest
import org.springframework.boot.test.mock.mockito.MockBean
import org.springframework.http.HttpHeaders
import org.springframework.http.MediaType
import org.springframework.http.ResponseCookie
import org.springframework.http.ResponseEntity
import org.springframework.restdocs.RestDocumentationContextProvider
import org.springframework.restdocs.RestDocumentationExtension
import org.springframework.restdocs.mockmvc.MockMvcRestDocumentation
import org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document
import org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.post
import org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath
import org.springframework.restdocs.payload.PayloadDocumentation.requestFields
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.header
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.status
import org.springframework.test.web.servlet.setup.DefaultMockMvcBuilder
import org.springframework.test.web.servlet.setup.MockMvcBuilders
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
    @WithCustomUser
    fun logout() {
        //given
        val cookie = ResponseCookie.from("refreshToken", "")
            .httpOnly(true)
            .secure(true)
            .sameSite("None")
            .path("/")
            .maxAge(0)
            .build().toString()

        given(
            userService.logout(
                anyLong()
            )
        ).willReturn(ResponseEntity.ok().header(HttpHeaders.SET_COOKIE, cookie).build())


        // when
        val result = this.mockMvc.perform(
            post("/logout")
        )

        // then
        result.andExpect(status().isOk)
            .andExpect(
                header().string(
                    "Set-cookie",
                    "refreshToken=; Path=/; Max-Age=0; Expires=Thu, 1 Jan 1970 00:00:00 GMT; Secure; HttpOnly; SameSite=None"
                )
            )
            .andDo(
                document(
                    "logout/200",
                )
            )
    }

    @Test
    fun refresh() {
        // given
        val refreshToken = ResponseCookie.from("refreshToken", "refreshToken")
            .httpOnly(true)
            .secure(true)
            .sameSite("None")
            .path("/")
            .maxAge(0)
            .build()
        val cookie = Cookie("refreshToken", refreshToken.toString())
        val newRefreshToken = ResponseCookie.from("refreshToken", "newRefreshToken")
            .httpOnly(true)
            .secure(true)
            .sameSite("None")
            .path("/")
            .maxAge(0)
            .build()
        given(
            userService.refresh(cookie)
        ).willReturn(ResponseEntity.ok().header(HttpHeaders.SET_COOKIE, newRefreshToken.toString()).build())


        // when
        val result = this.mockMvc.perform(
            post("/refresh")
                .cookie(cookie)
        )

        // then
        result.andExpect(status().isOk)
            .andExpect(
                header().string(
                    "Set-cookie",
                    "refreshToken=newRefreshToken; Path=/; Max-Age=0; Expires=Thu, 1 Jan 1970 00:00:00 GMT; Secure; HttpOnly; SameSite=None"
                )
            )
            .andDo(
                document(
                    "refresh/200",
                )
            )
    }
}
