package com.wafflestudio.ggzz.domain.global.common.exception

import com.fasterxml.jackson.annotation.JsonProperty
import com.ninjasquad.springmockk.MockkBean
import com.wafflestudio.ggzz.domain.ApiDocumentUtils.Companion.getDocumentResponse
import com.wafflestudio.ggzz.domain.global.common.exception.ExceptionDescribeSpec.ExceptionTestConfig.ExceptionTestController
import com.wafflestudio.ggzz.domain.global.common.exception.ExceptionDescribeSpec.ExceptionTestConfig.ExceptionTestService
import com.wafflestudio.ggzz.domain.letter.exception.LetterNotFoundException
import com.wafflestudio.ggzz.domain.letter.exception.LikeAlreadyExistsException
import io.kotest.core.annotation.DisplayName
import io.kotest.core.spec.style.DescribeSpec
import io.kotest.extensions.spring.SpringExtension
import io.mockk.every
import jakarta.validation.Valid
import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.NotNull
import jakarta.validation.constraints.Positive
import org.hamcrest.core.Is.`is`
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration
import org.springframework.boot.test.autoconfigure.restdocs.AutoConfigureRestDocs
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest
import org.springframework.boot.test.context.TestConfiguration
import org.springframework.http.ResponseEntity
import org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document
import org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.get
import org.springframework.stereotype.Service
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.status
import org.springframework.validation.annotation.Validated
import org.springframework.web.bind.annotation.*

@WebMvcTest(ExceptionTestController::class, excludeAutoConfiguration = [SecurityAutoConfiguration::class])
@AutoConfigureMockMvc
@AutoConfigureRestDocs
@DisplayName("Exception Rest Docs")
class ExceptionDescribeSpec(
    private val mockMvc: MockMvc,
    @MockkBean private val exceptionTestService: ExceptionTestService
): DescribeSpec() {
    override fun extensions() = listOf(SpringExtension)

    @TestConfiguration
    class ExceptionTestConfig {
        @Service
        class ExceptionTestService {
            fun error() {}
        }

        @Validated
        @RestController
        @RequestMapping("/error")
        class ExceptionTestController(private val exceptionTestService: ExceptionTestService) {
            @GetMapping("/{id}")
            fun constraintViolation(@PathVariable("id") @Positive id: Long): ResponseEntity<Any> {
                return ResponseEntity.ok().build()
            }

            @GetMapping
            fun serviceError(): ResponseEntity<Any> {
                exceptionTestService.error()
                return ResponseEntity.ok().build()
            }

            @PostMapping
            fun invalidField(@RequestBody @Valid request: ExceptionTestDto): ResponseEntity<Any> {
                return ResponseEntity.ok().build()
            }

            @GetMapping("/params")
            fun parameterTypeMismatch(@RequestParam int: Int): ResponseEntity<Any> {
                return ResponseEntity.ok().build()
            }
        }

        data class ExceptionTestDto(
            @field:[NotBlank]
            private val notBlankField: String?,
            @field:[NotNull Positive]
            private val nonNullablePositiveField: Int,
            @field:[Positive]
            private val nullablePositiveField: Int?,
            private val enumField: ExceptionEnum?
        )

        enum class ExceptionEnum {
            @JsonProperty("PROPER")
            PROPER
        }
    }

    init {
        this.describe("Letter 도메인") {
            context("끄적이 존재하지 않는 경우") {
                every { exceptionTestService.error() } throws LetterNotFoundException(1L)

                it("LETTER_NOT_FOUND: 에러코드 4000") {
                    mockMvc.perform(get("/error"))
                        .andDo(document(
                            "error/4000",
                            getDocumentResponse()))
                        .andExpect(status().isNotFound)
                        .andExpect(jsonPath("$.error_code", `is`(4000)))
                }
            }

            context("좋아요한 끄적에 다시 좋아요한 경우") {
                every { exceptionTestService.error() } throws LikeAlreadyExistsException(1L, 1L)

                it("LIKE_ALREADY_EXISTS: 에러코드 9001") {
                    mockMvc.perform(get("/error"))
                        .andDo(document(
                            "error/9001",
                            getDocumentResponse()))
                        .andExpect(status().isConflict)
                        .andExpect(jsonPath("$.error_code", `is`(9001)))
                }
            }
        }
    }

}