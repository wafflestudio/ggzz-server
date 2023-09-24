package com.wafflestudio.ggzz.domain.letter.controller

import com.ninjasquad.springmockk.MockkBean
import com.wafflestudio.ggzz.domain.auth.model.GgzzToken
import com.wafflestudio.ggzz.domain.letter.dto.LetterResponse
import com.wafflestudio.ggzz.domain.letter.service.MyLetterService
import com.wafflestudio.ggzz.global.common.dto.ListResponse
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
import java.time.LocalDateTime

@AutoConfigureRestDocs
@WebMvcTest(MyLetterController::class)
@MockkBean(JpaMetamodelMappingContext::class)
@DisplayName("MyLetterController 테스트")
class MyLetterControllerTest(
    private val mockMvc: MockMvc,
    @MockkBean private val myLetterService: MyLetterService
) : DescribeSpec() {

    override fun extensions() = listOf(SpringExtension)

    init {
        this.describe("내 끄적 조회") {
            every { myLetterService.getMyLetters(any()) } returns ListResponse(
                listOf(
                    LetterResponse(
                        id = 1L,
                        createdAt = LocalDateTime.now(),
                        createdBy = "username1",
                        longitude = 126.9779692,
                        latitude = 37.566535
                    ),
                    LetterResponse(
                        id = 2L,
                        createdAt = LocalDateTime.now(),
                        createdBy = "username2",
                        longitude = 126.9779692,
                        latitude = 37.566535
                    )
                )
            )

            context("GET /api/v1/me/letters") {
                it("200 OK") {
                    mockMvc.perform(
                        get("/api/v1/me/letters")
                            .with(authentication(GgzzToken.of(1L)))
                    ).andExpectAll(
                        status().isOk,
                        jsonPath("$.count").value(2),
                        jsonPath("$.data[0].id").value(1L),
                        jsonPath("$.data[0].createdAt").exists(),
                        jsonPath("$.data[0].createdBy").value("username1"),
                        jsonPath("$.data[0].longitude").value(126.9779692),
                        jsonPath("$.data[0].latitude").value(37.566535),
                        jsonPath("$.data[1].id").value(2L),
                        jsonPath("$.data[1].createdAt").exists(),
                        jsonPath("$.data[1].createdBy").value("username2"),
                        jsonPath("$.data[1].longitude").value(126.9779692),
                        jsonPath("$.data[1].latitude").value(37.566535)
                    ).andDo(
                        document(
                            "my-letter/list",
                            getDocumentRequest(),
                            getDocumentResponse()
                        )
                    ).andDo(MockMvcResultHandlers.print())
                }
            }
        }
    }

}
