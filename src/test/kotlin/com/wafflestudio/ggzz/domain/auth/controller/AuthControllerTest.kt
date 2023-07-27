package com.wafflestudio.ggzz.domain.auth.controller

import com.ninjasquad.springmockk.MockkBean
import com.wafflestudio.ggzz.domain.auth.dto.GgzzAuthResponse
import com.wafflestudio.ggzz.domain.auth.model.GgzzToken
import com.wafflestudio.ggzz.domain.auth.model.Provider
import com.wafflestudio.ggzz.domain.auth.service.AuthService
import com.wafflestudio.ggzz.domain.user.dto.UserBasicInfoResponse
import com.wafflestudio.ggzz.restdocs.ApiDocumentUtils.Companion.getDocumentRequest
import com.wafflestudio.ggzz.restdocs.ApiDocumentUtils.Companion.getDocumentResponse
import com.wafflestudio.ggzz.restdocs.ApiDocumentUtils.Companion.requestBody
import com.wafflestudio.ggzz.restdocs.ENUM
import com.wafflestudio.ggzz.restdocs.STRING
import com.wafflestudio.ggzz.restdocs.type
import io.kotest.core.annotation.DisplayName
import io.kotest.core.spec.style.DescribeSpec
import io.kotest.extensions.spring.SpringExtension
import io.mockk.every
import org.springframework.boot.test.autoconfigure.restdocs.AutoConfigureRestDocs
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest
import org.springframework.data.jpa.mapping.JpaMetamodelMappingContext
import org.springframework.http.HttpHeaders
import org.springframework.http.MediaType
import org.springframework.restdocs.headers.HeaderDocumentation.headerWithName
import org.springframework.restdocs.headers.HeaderDocumentation.requestHeaders
import org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document
import org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.post
import org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.authentication
import org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.result.MockMvcResultHandlers
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.status

@AutoConfigureRestDocs
@WebMvcTest(AuthController::class)
@MockkBean(JpaMetamodelMappingContext::class)
@DisplayName("AuthController 테스트")
class AuthControllerTest(
    private val mockMvc: MockMvc,
    @MockkBean private val authService: AuthService,
) : DescribeSpec() {

    override fun extensions() = listOf(SpringExtension)

    companion object {
        private val PROVIDERS = listOf(Provider.FIREBASE)
    }

    init {
        this.describe("끄적 회원가입") {
            every { authService.signup(any()) } returns GgzzAuthResponse(
                accessToken = "access_token",
                user = UserBasicInfoResponse(
                    ggzzId = "ggzzId",
                    firebaseId = null,
                    username = "username"
                )
            )

            context("POST /api/v1/auth/ggzz/signup") {
                it("200 OK") {
                    mockMvc.perform(
                        post("/api/v1/auth/ggzz/signup")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content("""
                                {
                                    "ggzzId": "ggzzId",
                                    "username": "username",
                                    "password": "raw_password"
                                }
                            """.trimIndent())
                            .with(csrf().asHeader())
                            .with(authentication(GgzzToken.of(1L)))
                    ).andExpectAll(
                        status().isOk,
                        jsonPath("$.accessToken").value("access_token"),
                        jsonPath("$.user.ggzzId").value("ggzzId"),
                        jsonPath("$.user.firebaseId").value(null),
                        jsonPath("$.user.username").value("username")
                    ).andDo(
                        document(
                            "auth/ggzz-signup",
                            getDocumentRequest(),
                            getDocumentResponse(),
                            requestBody(
                                "ggzzId"    type STRING means "끄적 아이디" formattedAs "Not Blank" example "ggzzId",
                                "username"  type STRING means "유저네임" formattedAs "Not Blank" example "username",
                                "password"  type STRING means "비밀번호" formattedAs "Not Blank" example "raw_password"
                            )
                        )
                    ).andDo(MockMvcResultHandlers.print())
                }
            }
        }

        this.describe("끄적 로그인") {
            every { authService.login(any()) } returns GgzzAuthResponse(
                accessToken = "access_token",
                user = UserBasicInfoResponse(
                    ggzzId = "ggzzId",
                    firebaseId = null,
                    username = "username"
                )
            )

            context("POST /api/v1/auth/ggzz/login") {
                it("200 OK") {
                    mockMvc.perform(
                        post("/api/v1/auth/ggzz/login")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content("""
                                {
                                    "ggzzId": "ggzzId",
                                    "password": "raw_password"
                                }
                            """.trimIndent())
                            .with(csrf().asHeader())
                            .with(authentication(GgzzToken.of(1L)))
                    ).andExpectAll(
                        status().isOk,
                        jsonPath("$.accessToken").value("access_token"),
                        jsonPath("$.user.ggzzId").value("ggzzId"),
                        jsonPath("$.user.firebaseId").value(null),
                        jsonPath("$.user.username").value("username")
                    ).andDo(
                        document(
                            "auth/ggzz-login",
                            getDocumentRequest(),
                            getDocumentResponse(),
                            requestBody(
                                "ggzzId"    type STRING means "끄적 아이디" formattedAs "Not Blank" example "ggzzId",
                                "password"  type STRING means "비밀번호" formattedAs "Not Blank" example "raw_password"
                            )
                        )
                    ).andDo(MockMvcResultHandlers.print())
                }
            }
        }

        this.describe("끄적 토큰 재발급") {
            every { authService.refresh(any()) } returns GgzzAuthResponse(
                accessToken = "new_access_token",
                user = UserBasicInfoResponse(
                    ggzzId = "ggzzId",
                    firebaseId = null,
                    username = "username"
                )
            )

            context("POST /api/v1/auth/ggzz/refresh") {
                it("200 OK") {
                    mockMvc.perform(
                        post("/api/v1/auth/ggzz/refresh")
                            .header(HttpHeaders.AUTHORIZATION, "Bearer access_token")
                            .with(csrf().asHeader())
                            .with(authentication(GgzzToken.of(1L)))
                    ).andExpectAll(
                        status().isOk,
                        jsonPath("$.accessToken").value("new_access_token"),
                        jsonPath("$.user.ggzzId").value("ggzzId"),
                        jsonPath("$.user.firebaseId").value(null),
                        jsonPath("$.user.username").value("username")
                    ).andDo(
                        document(
                            "auth/ggzz-refresh",
                            getDocumentRequest(),
                            getDocumentResponse(),
                            requestHeaders(
                                headerWithName(HttpHeaders.AUTHORIZATION).description("Bearer Token")
                            )
                        )
                    ).andDo(MockMvcResultHandlers.print())
                }
            }
        }

        this.describe("소셜 회원가입") {
            every { authService.signupWithProvider(any()) } returns GgzzAuthResponse(
                accessToken = "ggzz_access_token",
                user = UserBasicInfoResponse(
                    ggzzId = null,
                    firebaseId = "firebaseId",
                    username = "username"
                )
            )

            context("POST /api/v1/auth/provider/signup") {
                it("200 OK") {
                    mockMvc.perform(
                        post("/api/v1/auth/provider/signup")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content("""
                                {
                                    "accessToken": "Bearer firebase_token",
                                    "provider": "FIREBASE"
                                }
                            """.trimIndent())
                            .with(csrf().asHeader())
                            .with(authentication(GgzzToken.of(1L)))
                    ).andExpectAll(
                        status().isOk,
                        jsonPath("$.accessToken").value("ggzz_access_token"),
                        jsonPath("$.user.ggzzId").value(null),
                        jsonPath("$.user.firebaseId").value("firebaseId"),
                        jsonPath("$.user.username").value("username")
                    ).andDo(
                        document(
                            "auth/provider-signup",
                            getDocumentRequest(),
                            getDocumentResponse(),
                            requestBody(
                                "accessToken"   type STRING means "소셜 로그인 토큰" formattedAs "Not Blank: Bearer Token",
                                "provider"      type ENUM(PROVIDERS) means "소셜 로그인 종류" example "FIREBASE"
                            )
                        )
                    ).andDo(MockMvcResultHandlers.print())
                }
            }
        }

        this.describe("소셜 로그인") {
            every { authService.loginWithProvider(any()) } returns GgzzAuthResponse(
                accessToken = "ggzz_access_token",
                user = UserBasicInfoResponse(
                    ggzzId = null,
                    firebaseId = "firebaseId",
                    username = "username"
                )
            )

            context("POST /api/v1/auth/provider/login") {
                it("200 OK") {
                    mockMvc.perform(
                        post("/api/v1/auth/provider/login")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content("""
                                {
                                    "accessToken": "Bearer firebase_token",
                                    "provider": "FIREBASE"
                                }
                            """.trimIndent())
                            .with(csrf().asHeader())
                            .with(authentication(GgzzToken.of(1L)))
                    ).andExpectAll(
                        status().isOk,
                        jsonPath("$.accessToken").value("ggzz_access_token"),
                        jsonPath("$.user.ggzzId").value(null),
                        jsonPath("$.user.firebaseId").value("firebaseId"),
                        jsonPath("$.user.username").value("username")
                    ).andDo(
                        document(
                            "auth/provider-signin",
                            getDocumentRequest(),
                            getDocumentResponse(),
                            requestBody(
                                "accessToken"   type STRING means "소셜 로그인 토큰" formattedAs "Not Blank: Bearer Token",
                                "provider"      type ENUM(PROVIDERS) means "소셜 로그인 종류" example "FIREBASE"
                            )
                        )
                    ).andDo(MockMvcResultHandlers.print())
                }
            }
        }
    }
}
