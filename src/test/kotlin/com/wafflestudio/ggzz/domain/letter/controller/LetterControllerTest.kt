package com.wafflestudio.ggzz.domain.letter.controller

import aws.smithy.kotlin.runtime.util.encodeBase64
import com.ninjasquad.springmockk.MockkBean
import com.wafflestudio.ggzz.domain.auth.model.GgzzToken
import com.wafflestudio.ggzz.domain.letter.dto.LetterCreateRequest
import com.wafflestudio.ggzz.domain.letter.dto.LetterDetailResponse
import com.wafflestudio.ggzz.domain.letter.dto.LetterResponse
import com.wafflestudio.ggzz.domain.letter.service.LetterService
import com.wafflestudio.ggzz.global.common.dto.ListResponse
import com.wafflestudio.ggzz.restdocs.*
import com.wafflestudio.ggzz.restdocs.ApiDocumentUtils.Companion.getDocumentRequest
import com.wafflestudio.ggzz.restdocs.ApiDocumentUtils.Companion.getDocumentResponse
import com.wafflestudio.ggzz.restdocs.ApiDocumentUtils.Companion.pathParameter
import com.wafflestudio.ggzz.restdocs.ApiDocumentUtils.Companion.requestParameter
import com.wafflestudio.ggzz.restdocs.ApiDocumentUtils.Companion.requestPart
import io.kotest.core.annotation.DisplayName
import io.kotest.core.spec.style.DescribeSpec
import io.kotest.extensions.spring.SpringExtension
import io.mockk.every
import io.mockk.justRun
import io.mockk.slot
import org.springframework.boot.test.autoconfigure.restdocs.AutoConfigureRestDocs
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest
import org.springframework.data.jpa.mapping.JpaMetamodelMappingContext
import org.springframework.mock.web.MockPart
import org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document
import org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.*
import org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.authentication
import org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.result.MockMvcResultHandlers
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.status
import java.time.LocalDateTime

@AutoConfigureRestDocs
@WebMvcTest(LetterController::class)
@MockkBean(JpaMetamodelMappingContext::class)
@DisplayName("LetterController 테스트")
class LetterControllerTest(
    private val mockMvc: MockMvc,
    @MockkBean private val letterService: LetterService
) : DescribeSpec() {

    override fun extensions() = listOf(SpringExtension)

    init {
        this.describe("끄적 생성") {
            val requestSlot = slot<LetterCreateRequest>()
            every { letterService.postLetter(any(), capture(requestSlot)) } answers {
                val request = requestSlot.captured
                LetterDetailResponse(
                    id = 1L,
                    createdAt = LocalDateTime.now(),
                    createdBy = "username",
                    longitude = request.longitude!!,
                    latitude = request.latitude!!,
                    text = request.text,
                    image = "image-url",
                    voice = "voice-url"
                )
            }

            context("POST /api/v1/letters") {
                it("200 OK") {
                    mockMvc.perform(
                        multipart("/api/v1/letters")
                            .file("image", "image BLOB".encodeBase64().toByteArray())
                            .file("voice", "voice BLOB".encodeBase64().toByteArray())
                            .part(MockPart("longitude", "126.9779692".toByteArray()))
                            .part(MockPart("latitude", "37.566535".toByteArray()))
                            .part(MockPart("text", "끄적 생성".toByteArray()))
                            .with(csrf().asHeader())
                            .with(authentication(GgzzToken.of(1L)))
                    ).andExpectAll(
                        status().isOk,
                        jsonPath("$.id").value(1L),
                        jsonPath("$.createdAt").exists(),
                        jsonPath("$.createdBy").value("username"),
                        jsonPath("$.longitude").value(126.9779692),
                        jsonPath("$.latitude").value(37.566535),
                        jsonPath("$.text").value("끄적 생성"),
                        jsonPath("$.image").value("image-url"),
                        jsonPath("$.voice").value("voice-url"),
                    ).andDo(
                        document(
                            "letter/create",
                            getDocumentRequest(),
                            getDocumentResponse(),
                            requestPart(
                                "image"         partType FILE   means "이미지 파일" isOptional true,
                                "voice"         partType FILE   means "음성 파일" isOptional true,
                                "longitude"     partType DOUBLE means "경도" formattedAs "[-180,180]" example "126.9779692",
                                "latitude"      partType DOUBLE means "위도" formattedAs "[-90,90]" example "37.566535",
                                "text"          partType TEXT   means "끄적 내용" formattedAs "Not Blank" example "끄적 생성",
                                "viewableTime"  partType INT    means "조회 가능 시간" formattedAs "[0,24]" withDefaultValue "0" isOptional true,
                                "viewRange"     partType INT    means "조회 가능 범위 [m]" formattedAs "[0,200]" withDefaultValue "0" isOptional true
                            ),
                        )
                    ).andDo(MockMvcResultHandlers.print())
                }
            }
        }

        this.describe("끄적 리스트 조회") {
            every { letterService.getLetters(any(), any()) } returns ListResponse(
                listOf(
                    LetterResponse(
                        1L,
                        LocalDateTime.now(),
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

            context("GET /api/v1/letters") {
                it("200 OK") {
                    mockMvc.perform(
                        get("/api/v1/letters")
                            .param("longitude", "126.9779692")
                            .param("latitude", "37.566535")
                            .param("range", "100")
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
                        jsonPath("$.data[1].latitude").value(37.566535),
                    ).andDo(
                        document(
                            "letter/list",
                            getDocumentRequest(),
                            getDocumentResponse(),
                            requestParameter(
                                "longitude" parameterType DOUBLE means "현재 경도" formattedAs "[-180,180]" example "126.9779692",
                                "latitude"  parameterType DOUBLE means "현재 위도" formattedAs "[-90,90]" example "37.566535",
                                "range"     parameterType INT    means "조회 범위 [m]" formattedAs "[1,1000]" withDefaultValue "200" isOptional true
                            )
                        )
                    ).andDo(MockMvcResultHandlers.print())
                }
            }
        }

        this.describe("끄적 단건 조회") {
            every { letterService.getLetter(any(), any()) } returns LetterDetailResponse(
                id = 1L,
                createdAt = LocalDateTime.now(),
                createdBy = "username",
                longitude = 126.9779692,
                latitude = 37.566535,
                text = "끄적 내용",
                image = "image-url",
                voice = "voice-url"
            )

            context("GET /api/v1/letters/{id}") {
                it("200 OK") {
                    mockMvc.perform(
                        get("/api/v1/letters/{id}", 1L)
                            .param("longitude", "126.9779692")
                            .param("latitude", "37.566535")
                            .with(authentication(GgzzToken.of(1L)))
                    ).andExpectAll(
                        status().isOk,
                        jsonPath("$.id").value(1L),
                        jsonPath("$.createdAt").exists(),
                        jsonPath("$.createdBy").value("username"),
                        jsonPath("$.longitude").value(126.9779692),
                        jsonPath("$.latitude").value(37.566535),
                        jsonPath("$.text").value("끄적 내용"),
                        jsonPath("$.image").value("image-url"),
                        jsonPath("$.voice").value("voice-url"),
                    ).andDo(
                        document(
                            "letter/detail",
                            getDocumentRequest(),
                            getDocumentResponse(),
                            pathParameter(
                                "id" parameterType LONG means "끄적 ID" example "1"
                            ),
                            requestParameter(
                                "longitude" parameterType DOUBLE means "현재 경도" formattedAs "[-180,180]" example "126.9779692",
                                "latitude"  parameterType DOUBLE means "현재 위도" formattedAs "[-90,90]" example "37.566535"
                            )
                        )
                    ).andDo(MockMvcResultHandlers.print())
                }
            }
        }

        this.describe("끄적 삭제") {
            justRun { letterService.deleteLetter(any(), any()) }

            context("DELETE /api/v1/letters/{id}") {
                it("200 OK") {
                    mockMvc.perform(
                        delete("/api/v1/letters/{id}", 1L)
                            .with(csrf().asHeader())
                            .with(authentication(GgzzToken.of(1L)))
                    ).andExpectAll(
                        status().isOk
                    ).andDo(
                        document(
                            "letter/delete",
                            getDocumentRequest(),
                            getDocumentResponse(),
                            pathParameter(
                                "id" parameterType LONG means "끄적 ID" example "1"
                            )
                        )
                    ).andDo(MockMvcResultHandlers.print())
                }
            }
        }

    }

}
