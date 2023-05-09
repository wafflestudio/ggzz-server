package com.wafflestudio.nostalgia.domain.letter.controller

import com.fasterxml.jackson.databind.ObjectMapper
import com.wafflestudio.nostalgia.domain.ApiDocumentUtils
import com.wafflestudio.nostalgia.domain.ApiDocumentUtils.Companion.getDocumentRequest
import com.wafflestudio.nostalgia.domain.ApiDocumentUtils.Companion.getDocumentResponse
import com.wafflestudio.nostalgia.domain.WithCustomUser
import com.wafflestudio.nostalgia.domain.letter.dto.LetterDto
import com.wafflestudio.nostalgia.domain.letter.model.Letter
import com.wafflestudio.nostalgia.domain.letter.service.LetterService
import com.wafflestudio.nostalgia.domain.user.model.CurrentUser
import com.wafflestudio.nostalgia.domain.user.model.User
import com.wafflestudio.nostalgia.domain.user.model.UserPrincipal
import com.wafflestudio.nostalgia.domain.user.model.UserToken
import com.wafflestudio.nostalgia.domain.user.repository.UserRepository
import com.wafflestudio.nostalgia.global.common.dto.ListResponse
import org.junit.jupiter.api.Test

import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.ArgumentMatchers.any
import org.mockito.ArgumentMatchers.anyLong
import org.mockito.BDDMockito.given
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.mock.mockito.MockBean
import org.springframework.http.MediaType
import org.springframework.restdocs.RestDocumentationContextProvider
import org.springframework.restdocs.RestDocumentationExtension
import org.springframework.restdocs.mockmvc.MockMvcRestDocumentation
import org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document
import org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.get
import org.springframework.restdocs.payload.PayloadDocumentation
import org.springframework.restdocs.payload.PayloadDocumentation.*
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.authentication
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post
import org.springframework.test.web.servlet.request.RequestPostProcessor
import org.springframework.test.web.servlet.result.MockMvcResultHandlers
import org.springframework.test.web.servlet.result.MockMvcResultMatchers
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.status
import org.springframework.test.web.servlet.setup.DefaultMockMvcBuilder
import org.springframework.test.web.servlet.setup.MockMvcBuilders
import org.springframework.transaction.annotation.Transactional
import org.springframework.web.context.WebApplicationContext
import java.time.LocalDateTime

@ExtendWith(RestDocumentationExtension::class)
@WebMvcTest(MyLetterController::class)
internal class MyLetterControllerTest {

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
    fun getMyLetters() {
        // given
        val letters = mutableListOf<LetterDto.Response>()
        letters.add(LetterDto.Response(0L, LocalDateTime.now(), "Junhyeong Kim", "Letter 1", "summary", 127.0, 37.0))
        letters.add(LetterDto.Response(1L, LocalDateTime.now(), "Junhyeong Kim", "Letter 2", "summary", 127.0, 37.0))
        letters.add(LetterDto.Response(2L, LocalDateTime.now(), "Junhyeong Kim", "Letter 3", "summary", 127.0, 37.0))
        val response = ListResponse(letters)
        given(letterService.getMyLetters(anyLong())).willReturn(response)

        // when
        val result = this.mockMvc.perform(
            get("/api/v1/me/letters")
                .accept(MediaType.APPLICATION_JSON)
        ).andDo(MockMvcResultHandlers.print())

        // then
        result.andExpect(status().isOk)
            .andDo(
                document(
                    "getMyLetters/200",
                    getDocumentRequest(),
                    getDocumentResponse(),
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

}
