package com.wafflestudio.ggzz.domain.letter.controller

import com.ninjasquad.springmockk.MockkBean
import com.wafflestudio.ggzz.domain.auth.model.GgzzToken
import com.wafflestudio.ggzz.domain.letter.dto.LetterResponse
import com.wafflestudio.ggzz.domain.letter.service.LetterInteractionService
import com.wafflestudio.ggzz.restdocs.ApiDocumentUtils.Companion.getDocumentRequest
import com.wafflestudio.ggzz.restdocs.ApiDocumentUtils.Companion.getDocumentResponse
import com.wafflestudio.ggzz.restdocs.ApiDocumentUtils.Companion.pathParameter
import com.wafflestudio.ggzz.restdocs.LONG
import com.wafflestudio.ggzz.restdocs.parameterType
import io.kotest.core.annotation.DisplayName
import io.kotest.core.spec.style.DescribeSpec
import io.kotest.extensions.spring.SpringExtension
import io.mockk.every
import org.springframework.boot.test.autoconfigure.restdocs.AutoConfigureRestDocs
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest
import org.springframework.data.jpa.mapping.JpaMetamodelMappingContext
import org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document
import org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.delete
import org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.post
import org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.authentication
import org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.status
import java.time.LocalDateTime

@AutoConfigureRestDocs
@WebMvcTest(LetterInteractionController::class)
@MockkBean(JpaMetamodelMappingContext::class)
@DisplayName("LetterInteractionController 테스트")
class LetterInteractionControllerTest(
    private val mockMvc: MockMvc,
    @MockkBean private val letterInteractionService: LetterInteractionService
) : DescribeSpec() {

    override fun extensions() = listOf(SpringExtension)

    init {
        this.describe("끄적 좋아요") {
            every { letterInteractionService.likeLetter(any(), any()) } returns LetterResponse(
                id = 1L,
                createdAt = LocalDateTime.now(),
                createdBy = "username",
                longitude = 126.9779692,
                latitude = 37.566535
            )

            context("PUT /api/v1/letters/{id}/like") {
                it("200 OK") {
                    mockMvc.perform(
                        post("/api/v1/letters/{id}/like", 1L)
                            .with(csrf().asHeader())
                            .with(authentication(GgzzToken.of(1L)))
                    ).andExpectAll(
                        status().isOk,
                    ).andDo(
                        document(
                            "letter-interaction/like",
                            getDocumentRequest(),
                            getDocumentResponse(),
                            pathParameter(
                                "id" parameterType LONG means "끄적 ID" example "1"
                            )
                        )
                    )
                }
            }
        }

        this.describe("끄적 좋아요 취소") {
            every { letterInteractionService.unlikeLetter(any(), any()) } returns LetterResponse(
                id = 1L,
                createdAt = LocalDateTime.now(),
                createdBy = "username",
                longitude = 126.9779692,
                latitude = 37.566535
            )

            context("DELETE /api/v1/letters/{id}/like") {
                it("200 OK") {
                    mockMvc.perform(
                        delete("/api/v1/letters/{id}/like", 1L)
                            .with(csrf().asHeader())
                            .with(authentication(GgzzToken.of(1L)))
                    ).andExpectAll(
                        status().isOk,
                    ).andDo(
                        document(
                            "letter-interaction/delete-like",
                            getDocumentRequest(),
                            getDocumentResponse(),
                            pathParameter(
                                "id" parameterType LONG means "끄적 ID" example "1"
                            )
                        )
                    )
                }
            }
        }
    }

}
