package com.wafflestudio.nostalgia.domain.letter.controller

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.wafflestudio.nostalgia.domain.ApiDocumentUtils.Companion.getDocumentRequest
import com.wafflestudio.nostalgia.domain.ApiDocumentUtils.Companion.getDocumentResponse
import com.wafflestudio.nostalgia.domain.WithCustomUser
import com.wafflestudio.nostalgia.domain.letter.dto.LetterDto
import com.wafflestudio.nostalgia.domain.letter.model.Letter
import com.wafflestudio.nostalgia.domain.letter.service.LetterService
import com.wafflestudio.nostalgia.domain.user.model.UserPrincipal
import com.wafflestudio.nostalgia.global.common.dto.ListResponse
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.ArgumentMatchers.*
import org.mockito.BDDMockito.given
import org.mockito.kotlin.any
import org.mockito.kotlin.doNothing
import org.mockito.kotlin.whenever
import org.springframework.beans.factory.annotation.Autowired

import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest
import org.springframework.boot.test.mock.mockito.MockBean
import org.springframework.http.MediaType
import org.springframework.mock.web.MockMultipartFile
import org.springframework.restdocs.RestDocumentationContextProvider
import org.springframework.restdocs.RestDocumentationExtension
import org.springframework.restdocs.mockmvc.MockMvcRestDocumentation
import org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document
import org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.*
import org.springframework.restdocs.payload.PayloadDocumentation.*
import org.springframework.restdocs.request.RequestDocumentation.*
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.result.MockMvcResultHandlers
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.status
import org.springframework.test.web.servlet.setup.DefaultMockMvcBuilder
import org.springframework.test.web.servlet.setup.MockMvcBuilders
import org.springframework.web.context.WebApplicationContext
import java.time.LocalDateTime

@ExtendWith(RestDocumentationExtension::class)
@WebMvcTest(LetterController::class)
internal class LetterControllerTest {

    @Autowired
    private lateinit var objectMapper: ObjectMapper

    @MockBean
    private lateinit var letterService: LetterService

    private lateinit var mockMvc: MockMvc

    @BeforeEach
    fun setUp(webApplicationContext: WebApplicationContext, restDocumentation: RestDocumentationContextProvider) {
        this.mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext)
            .apply<DefaultMockMvcBuilder>(MockMvcRestDocumentation.documentationConfiguration(restDocumentation))
            .build()
    }

    @Test
    @WithCustomUser
    fun postLetter() {
        // given
        val userPrincipal = SecurityContextHolder.getContext().authentication.principal as UserPrincipal
        val user = userPrincipal.user

        val request = LetterDto.CreateRequest(
            title = "Hello",
            summary = "summary",
            longitude = 126.0,
            latitude = 37.0,
            text = "text"
        )

        given(
            letterService.postLetter(
                anyLong(),
                any()
            )
        ).willReturn(LetterDto.Response(Letter(user, request)))

        // when
        val result = this.mockMvc.perform(
            post("/api/v1/letters")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request))
                .accept(MediaType.APPLICATION_JSON)
        ).andDo(MockMvcResultHandlers.print())

        // then
        result.andExpect(status().isOk)
            .andDo(
                document(
                    "postLetter/200",
                    getDocumentRequest(),
                    getDocumentResponse(),
                    requestFields(
                        fieldWithPath("title").description("편지 제목"),
                        fieldWithPath("summary").description("편지 요약"),
                        fieldWithPath("longitude").description("경도"),
                        fieldWithPath("latitude").description("위도"),
                        fieldWithPath("text").description("편지 내용(글)").optional()
                    ),
                    responseFields(
                        fieldWithPath("id").description("API에 사용되는 편지 ID"),
                        fieldWithPath("created_at").description("편지 생성일자"),
                        fieldWithPath("created_by").description("편지 생성자 닉네임"),
                        fieldWithPath("title").description("편지 제목"),
                        fieldWithPath("summary").description("편지 내용"),
                        fieldWithPath("longitude").description("경도"),
                        fieldWithPath("latitude").description("위도")
                    )
                )
            )
    }

    @Test
    fun getLetters() {
        // given
        val letters = mutableListOf<LetterDto.Response>()
        letters.add(LetterDto.Response(0L, LocalDateTime.now(), "Junhyeong Kim", "Letter 1", "summary", 127.0, 37.0))
        letters.add(LetterDto.Response(1L, LocalDateTime.now(), "Simon Kim", "Letter 2", "summary", 127.0, 37.0))
        letters.add(LetterDto.Response(2L, LocalDateTime.now(), "Junhyeong Kim", "Letter 3", "summary", 127.0, 37.0))
        val response = ListResponse(letters)
        given(
            letterService.getLetters(
                any(),
                anyInt()
            )
        ).willReturn(response)

        // when
        val result = this.mockMvc.perform(
            get("/api/v1/letters")
                .param("longitude", "127.0")
                .param("latitude", "37.0")
                .param("range", "200")
                .accept(MediaType.APPLICATION_JSON)
        ).andDo(MockMvcResultHandlers.print())

        // then
        result.andExpect(status().isOk)
            .andDo(
                document(
                    "getLetters/200",
                    getDocumentRequest(),
                    getDocumentResponse(),
                    queryParameters(
                        parameterWithName("longitude").description("경도"),
                        parameterWithName("latitude").description("위도"),
                        parameterWithName("range").description("검색 범위").optional()
                    ),
                    responseFields(
                        fieldWithPath("count").description("내 편지 개수"),
                        fieldWithPath("data[].id").description("API에 사용되는 편지 ID"),
                        fieldWithPath("data[].created_at").description("편지 생성일자"),
                        fieldWithPath("data[].created_by").description("편지 생성자 닉네임"),
                        fieldWithPath("data[].title").description("편지 제목"),
                        fieldWithPath("data[].summary").description("편지 내용"),
                        fieldWithPath("data[].longitude").description("경도"),
                        fieldWithPath("data[].latitude").description("위도")
                    )
                )
            )
    }

    @Test
    fun getLetter() {
        // given
        val response = LetterDto.DetailResponse(
            id = 12,
            createdAt = LocalDateTime.now(),
            createdBy = "Someone",
            title = "New Letter",
            summary = "blah blah",
            longitude = 127.0,
            latitude = 37.0,
            text = "Hello",
            image = null,
            voice = null
        )
        given(
            letterService.getLetter(
                anyLong(),
                any()
            )
        ).willReturn(response)

        // when
        val result = this.mockMvc.perform(
            get("/api/v1/letters/{id}", 12)
                .param("longitude", "127.0")
                .param("latitude", "37.0")
                .accept(MediaType.APPLICATION_JSON)
        ).andDo(MockMvcResultHandlers.print())

        // then
        result.andExpect(status().isOk)
            .andDo(
                document(
                    "getLetter/200",
                    getDocumentRequest(),
                    getDocumentResponse(),
                    pathParameters(
                        parameterWithName("id").description("편지 ID")
                    ),
                    queryParameters(
                        parameterWithName("longitude").description("경도"),
                        parameterWithName("latitude").description("위도")
                    ),
                    responseFields(
                        fieldWithPath("id").description("API에 사용되는 편지 ID"),
                        fieldWithPath("created_at").description("편지 생성일자"),
                        fieldWithPath("created_by").description("편지 생성자 닉네임"),
                        fieldWithPath("title").description("편지 제목"),
                        fieldWithPath("summary").description("편지 내용"),
                        fieldWithPath("longitude").description("경도"),
                        fieldWithPath("latitude").description("위도"),
                        fieldWithPath("text").description("편지 내용(글)").optional(),
                        fieldWithPath("image").description("편지 내용(이미지)").optional(),
                        fieldWithPath("voice").description("편지 내용(음성)").optional()
                    )
                )
            )
    }

    @Test
    @WithCustomUser
    fun deleteLetter() {

        // when
        val result = this.mockMvc.perform(
            delete("/api/v1/letters/{id}", 12)
        ).andDo(MockMvcResultHandlers.print())

        // then
        result.andExpect(status().isOk)
            .andDo(
                document(
                    "deleteLetter/200",
                    getDocumentRequest(),
                    getDocumentResponse(),
                    pathParameters(
                        parameterWithName("id").description("편지 ID")
                    )
                )
            )

    }

    @Test
    @WithCustomUser
    fun putResource() {
        // given
        val imageContent = ByteArray(100)
        val imageFile = MockMultipartFile("image", "test.png", MediaType.IMAGE_PNG_VALUE, imageContent)
        val voiceContent = ByteArray(100)
        val voiceFile = MockMultipartFile("voice", "test.mp3", "audio/mpeg", voiceContent)

        // when
        val result = this.mockMvc.perform(
            multipart("/api/v1/letters/{id}/source", 12)
                .file(imageFile)
                .file(voiceFile)
                .with { request -> request.method = "PUT"; request }
                .accept(MediaType.APPLICATION_JSON)
        ).andDo(MockMvcResultHandlers.print())

        // then
        result.andExpect(status().isOk)
            .andDo(
                document(
                    "putResource/200",
                    getDocumentRequest(),
                    getDocumentResponse(),
                    pathParameters(
                        parameterWithName("id").description("편지 ID")
                    ),
                    requestParts(
                        partWithName("image").description("이미지 파일").optional(),
                        partWithName("voice").description("음성 파일").optional()
                    ),
                    responseFields(
                        fieldWithPath("image").description("이미지 업로드"),
                        fieldWithPath("voice").description("음성 업로드")
                    )
                )
            )
    }

}
