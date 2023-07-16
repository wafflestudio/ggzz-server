package com.wafflestudio.ggzz.domain.letter.controller

import com.wafflestudio.ggzz.domain.letter.dto.LetterDto
import com.wafflestudio.ggzz.domain.letter.service.LetterService
import com.wafflestudio.ggzz.domain.user.dto.UserDto
import com.wafflestudio.ggzz.domain.user.model.User
import com.wafflestudio.ggzz.domain.user.repository.UserRepository
import com.wafflestudio.ggzz.domain.user.service.UserService
import com.wafflestudio.ggzz.global.common.dto.ListResponse
import com.wafflestudio.ggzz.global.config.FirebaseConfig
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.mockito.ArgumentMatchers.*
import org.mockito.Mockito.mock
import org.mockito.Mockito.`when`
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest
import org.springframework.boot.test.mock.mockito.MockBean
import org.springframework.http.MediaType
import org.springframework.mock.web.MockMultipartFile
import org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.*
import org.springframework.restdocs.operation.preprocess.Preprocessors.*
import org.springframework.restdocs.payload.PayloadDocumentation.*
import org.springframework.restdocs.request.RequestDocumentation.*
import org.springframework.security.core.Authentication
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.status
import java.time.LocalDateTime
import org.springframework.security.core.userdetails.User as SecurityUser

@WebMvcTest(LetterController::class)
@AutoConfigureMockMvc
class LetterControllerTest @Autowired constructor(
    val mockMvc: MockMvc
) {
    @MockBean
    lateinit var letterService: LetterService

    @MockBean
    lateinit var userService: UserService

    @MockBean
    lateinit var userRepository: UserRepository

    @MockBean
    lateinit var firebaseConfig: FirebaseConfig

    @BeforeEach
    fun setAuthentication() {
        val signUpRequest = UserDto.SignUpRequest("test_username", "test_nickname", "test_password")
        val createdUser = User("test_firebase_id", "test_username", "test_nickname", "encoded_password")

        // userService.updateOrCreate() 메서드의 Mock 설정
        `when`(userService.updateOrCreate(signUpRequest)).thenReturn(createdUser)

        // firebaseConfig.getIdByToken() 메서드의 Mock 설정
        `when`(firebaseConfig.getIdByToken(anyString())).thenReturn("test_firebase_id")

        // 인증된 사용자 설정
        val authentication = mock(Authentication::class.java)
        `when`(authentication.isAuthenticated).thenReturn(true)
        `when`(authentication.principal).thenReturn(SecurityUser("test_username", "test_password", emptyList()))

        // SecurityContextHolder에 인증 정보 설정
        SecurityContextHolder.getContext().authentication = authentication
    }


    @AfterEach
    fun cleanSecurityContext() {
        SecurityContextHolder.clearContext() // 인증 정보 제거
    }

    @Test
    fun postLetterTest() {
        // POST 요청을 수행하고 응답을 검증
        val result = this.mockMvc.perform(
            MockMvcRequestBuilders.post("/api/v1/letters")
                .contentType(MediaType.APPLICATION_JSON)
                .content(
                    """{
                    |"title":"test_title",
                    |"summary":"test_summary",
                    |"text":"test_text",
                    |"longitude": 126.0,
                    |"latitude": 37.0
                    |}""".trimMargin()
                )
                .header("Authorization", "Bearer test_token")
                .with(csrf())
        )

        // then
        result.andExpect(status().isOk)
//            .andDo(
//                document(
//                    "postLetter/200",
//                    getDocumentRequest(),
//                    getDocumentResponse(),
//                    requestFields(
//                        fieldWithPath("title").description("편지 제목"),
//                        fieldWithPath("summary").description("편지 요약"),
//                        fieldWithPath("longitude").description("경도"),
//                        fieldWithPath("latitude").description("위도"),
//                        fieldWithPath("text").description("편지 내용(글)").optional(),
//                        fieldWithPath("viewable_time").description("편지 공개 시간").optional(),
//                        fieldWithPath("view_range").description("편지 공개 범위").optional()
//                    ),
//                    responseFields(
//                        fieldWithPath("id").description("API에 사용되는 편지 ID"),
//                        fieldWithPath("created_at").description("편지 생성일자"),
//                        fieldWithPath("created_by").description("편지 생성자 닉네임"),
//                        fieldWithPath("title").description("편지 제목"),
//                        fieldWithPath("summary").description("편지 내용"),
//                        fieldWithPath("longitude").description("경도"),
//                        fieldWithPath("latitude").description("위도")
//                    )
//                )
//            )
    }

    @Test
    fun getLettersTest() {
        // given
        val letters = mutableListOf<LetterDto.Response>()
        letters.add(LetterDto.Response(0L, LocalDateTime.now(), "Junhyeong Kim", "Letter 1", "summary", 127.0, 37.0))
        letters.add(LetterDto.Response(1L, LocalDateTime.now(), "Yeonghyeon Ko", "Letter 2", "summary", 127.0, 37.0))
        val response = ListResponse(letters)
        `when`(
            letterService.getLetters(
                any(),
                anyInt()
            )
        ).thenReturn(response)

        // when
        val result = this.mockMvc.perform(
            get("/api/v1/letters")
                .param("longitude", "127.0")
                .param("latitude", "37.0")
                .param("range", "200")
                .accept(MediaType.APPLICATION_JSON)
                .header("Authorization", "Bearer test_token")
        )

        // then
        result.andExpect(status().isOk)
    }

    @Test
    fun getLetterTest() {
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
        `when`(
            letterService.getLetter(
                anyLong(),
                any()
            )
        ).thenReturn(response)

        // when
        val result = this.mockMvc.perform(
            get("/api/v1/letters/{id}", 12)
                .param("longitude", "127.0")
                .param("latitude", "37.0")
                .accept(MediaType.APPLICATION_JSON)
                .header("Authorization", "Bearer test_token")
        )

        // then
        result.andExpect(status().isOk)
    }

    @Test
    fun deleteLetterTest() {
        // when
        val result = this.mockMvc.perform(
            delete("/api/v1/letters/{id}", 12)
                .header("Authorization", "Bearer test_token")
                .with(csrf())
        )

        // then
        result.andExpect(status().isOk)
    }

    @Test
    fun putResourceTest() {
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
                .header("Authorization", "Bearer test_token")
                .with(csrf())
        )

        // then
        result.andExpect(status().isOk)
    }
}