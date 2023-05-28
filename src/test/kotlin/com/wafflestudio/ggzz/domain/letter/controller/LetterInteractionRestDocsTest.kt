package com.wafflestudio.ggzz.domain.letter.controller

import com.ninjasquad.springmockk.MockkBean
import com.wafflestudio.ggzz.domain.ApiDocumentUtils.Companion.getDocumentRequest
import com.wafflestudio.ggzz.domain.ApiDocumentUtils.Companion.getDocumentResponse
import com.wafflestudio.ggzz.domain.letter.dto.LetterDto
import com.wafflestudio.ggzz.domain.letter.model.Letter
import com.wafflestudio.ggzz.domain.letter.service.LetterInteractionService
import com.wafflestudio.ggzz.domain.user.model.User
import com.wafflestudio.ggzz.domain.user.model.UserPrincipal
import com.wafflestudio.ggzz.domain.user.model.UserToken
import io.kotest.core.annotation.DisplayName
import io.kotest.core.spec.style.DescribeSpec
import io.kotest.core.test.TestCase
import io.kotest.extensions.spring.SpringExtension
import io.mockk.every
import org.springframework.boot.test.autoconfigure.restdocs.AutoConfigureRestDocs
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest
import org.springframework.data.jpa.mapping.JpaMetamodelMappingContext
import org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document
import org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.delete
import org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.put
import org.springframework.restdocs.request.RequestDocumentation.parameterWithName
import org.springframework.restdocs.request.RequestDocumentation.pathParameters
import org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.authentication
import org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.result.MockMvcResultHandlers.print
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.status

@WebMvcTest(LetterInteractionController::class)
@AutoConfigureMockMvc
@AutoConfigureRestDocs
@MockkBean(JpaMetamodelMappingContext::class)
@DisplayName("LetterInteractionController Rest Docs")
class LetterInteractionRestDocsTest(
    private val mockMvc: MockMvc,
    @MockkBean private val letterInteractionService: LetterInteractionService
): DescribeSpec() {
    override fun extensions() = listOf(SpringExtension)

    companion object {
        private val user = User("username", "nickname", "password")
        private val letter = Letter(user, "title", "summary", 0.0, 0.0, "text", "image", "voice")
        private val auth = UserToken(UserPrincipal(user))
    }

    override suspend fun beforeContainer(testCase: TestCase) {
        every { letterInteractionService.likeLetter(any(), any()) } returns LetterDto.Response(letter)
        every { letterInteractionService.unlikeLetter(any(), any()) } returns LetterDto.Response(letter)
    }

    init {
        this.describe("좋아요 API") {
            context("정상적인 호출인 경우") {
                it("200 OK: LetterDto.Response") {
                    letter.numberOfLikes = 1

                    mockMvc.perform(
                        put("/api/v1/letters/{letter-id}/like", 1L)
                            .with(authentication(auth)).with(csrf())
                    ).andDo(document(
                        "letter-interaction/like",
                        getDocumentRequest(),
                        getDocumentResponse(),
                        pathParameters(parameterWithName("letter-id").description("좋아요할 편지의 ID")))
                    ).andDo(print()
                    ).andExpect(status().isOk())
                }
            }
        }

        this.describe("좋아요 취소 API") {
            context("정상적인 호출인 경우") {
                it("200 OK: LetterDto.Response") {
                    letter.numberOfLikes = 0

                    mockMvc.perform(
                        delete("/api/v1/letters/{letter-id}/like", 1L)
                            .with(authentication(auth)).with(csrf())
                    ).andDo(document(
                        "letter-interaction/unlike",
                        getDocumentRequest(),
                        getDocumentResponse(),
                        pathParameters(parameterWithName("letter-id").description("좋아요 취소할 편지의 ID")))
                    ).andDo(print()
                    ).andExpect(status().isOk())
                }
            }
        }
    }

}
