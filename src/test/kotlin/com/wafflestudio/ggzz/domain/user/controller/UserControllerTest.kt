package com.wafflestudio.ggzz.domain.user.controller

import com.ninjasquad.springmockk.MockkBean
import com.wafflestudio.ggzz.domain.auth.model.GgzzToken
import com.wafflestudio.ggzz.domain.user.dto.UserBasicInfoResponse
import com.wafflestudio.ggzz.domain.user.service.UserService
import com.wafflestudio.ggzz.restdocs.ApiDocumentUtils.Companion.getDocumentRequest
import com.wafflestudio.ggzz.restdocs.ApiDocumentUtils.Companion.getDocumentResponse
import io.kotest.core.annotation.DisplayName
import io.kotest.core.spec.style.DescribeSpec
import io.kotest.extensions.spring.SpringExtension
import io.mockk.every
import org.springframework.boot.test.autoconfigure.restdocs.AutoConfigureRestDocs
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest
import org.springframework.data.jpa.mapping.JpaMetamodelMappingContext
import org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document
import org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.get
import org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.authentication
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.result.MockMvcResultHandlers
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.status

@AutoConfigureRestDocs
@WebMvcTest(UserController::class)
@MockkBean(JpaMetamodelMappingContext::class)
@DisplayName("UserController 테스트")
class UserControllerTest(
    private val mockMvc: MockMvc,
    @MockkBean private val userService: UserService
) : DescribeSpec() {

    override fun extensions() = listOf(SpringExtension)

    init {
        this.describe("내 정보 조회") {
            every { userService.getMe(any()) } returns UserBasicInfoResponse(
                ggzzId = "ggzzId",
                firebaseId = "firebaseId",
                username = "username"
            )

            context("GET /api/v1/users/me") {
                it("200 OK") {
                    mockMvc.perform(
                        get("/api/v1/users/me")
                            .with(authentication(GgzzToken.of(1L)))
                    ).andExpectAll(
                        status().isOk,
                        jsonPath("$.ggzzId").value("ggzzId"),
                        jsonPath("$.firebaseId").value("firebaseId"),
                        jsonPath("$.username").value("username")
                    ).andDo(
                        document(
                            "user/me",
                            getDocumentRequest(),
                            getDocumentResponse()
                        )
                    ).andDo(MockMvcResultHandlers.print())
                }
            }
        }
    }

}
