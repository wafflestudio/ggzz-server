package com.wafflestudio.ggzz.domain.global.common.exception

import com.fasterxml.jackson.annotation.JsonProperty
import com.ninjasquad.springmockk.MockkBean
import com.wafflestudio.ggzz.domain.auth.exception.*
import com.wafflestudio.ggzz.domain.auth.model.Provider
import com.wafflestudio.ggzz.domain.global.common.exception.ExceptionDescribeSpec.ExceptionTestConfig.ExceptionTestController
import com.wafflestudio.ggzz.domain.global.common.exception.ExceptionDescribeSpec.ExceptionTestConfig.ExceptionTestService
import com.wafflestudio.ggzz.domain.letter.exception.*
import com.wafflestudio.ggzz.domain.user.exception.UserNotFoundException
import com.wafflestudio.ggzz.global.common.exception.InternalServerError
import com.wafflestudio.ggzz.restdocs.ApiDocumentUtils.Companion.getDocumentResponse
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
import org.springframework.http.MediaType
import org.springframework.http.ResponseEntity
import org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document
import org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.get
import org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.post
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
        this.describe("Bad Request") {
            context("Request Body Validation 실패한 경우") {
                it("INVALID_FIELD") {
                    mockMvc.perform(post("/error")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                            {
                                "notBlankField": "",
                                "nonNullablePositiveField": null,
                                "nullablePositiveField": -1,
                                "enumField": null
                            }
                        """.trimIndent()))
                        .andDo(document(
                            "error/0000",
                            getDocumentResponse()))
                        .andExpect(status().isBadRequest)
                        .andExpect(jsonPath("$.errorCode", `is`(0)))
                }
            }

            context("Request Parameter Validation 실패한 경우") {
                it("CONSTRAINT_VIOLATION") {
                    mockMvc.perform(get("/error/-1"))
                        .andDo(document(
                            "error/0001",
                            getDocumentResponse()))
                        .andExpect(status().isBadRequest)
                        .andExpect(jsonPath("$.errorCode", `is`(1)))
                }
            }

            context("지원되지 않는 파일 타입인 경우") {
                every { exceptionTestService.error() } throws UnsupportedFileTypeException("jpeg", "image/png")

                it("UNSUPPORTED_FILE_TYPE") {
                    mockMvc.perform(get("/error"))
                        .andDo(document(
                            "error/0002",
                            getDocumentResponse()))
                        .andExpect(status().isBadRequest)
                        .andExpect(jsonPath("$.errorCode", `is`(2)))
                }
            }

            context("파일 크기가 너무 큰 경우") {
                every { exceptionTestService.error() } throws FileTooLargeException()

                it("FILE_TOO_LARGE") {
                    mockMvc.perform(get("/error"))
                        .andDo(document(
                            "error/0003",
                            getDocumentResponse()))
                        .andExpect(status().isBadRequest)
                        .andExpect(jsonPath("$.errorCode", `is`(3)))
                }
            }

            context("올바른 토큰이 아닌 경우") {
                every { exceptionTestService.error() } throws InvalidTokenException()

                it("INVALID_TOKEN") {
                    mockMvc.perform(get("/error"))
                        .andDo(document(
                            "error/0100",
                            getDocumentResponse()))
                        .andExpect(status().isBadRequest)
                        .andExpect(jsonPath("$.errorCode", `is`(100)))
                }
            }

            context("올바른 토큰 공급자가 아닌 경우") {
                every { exceptionTestService.error() } throws InvalidProviderException(Provider.FIREBASE)

                it("INVALID_PROVIDER") {
                    mockMvc.perform(get("/error"))
                        .andDo(document(
                            "error/0101",
                            getDocumentResponse()))
                        .andExpect(status().isBadRequest)
                        .andExpect(jsonPath("$.errorCode", `is`(101)))
                }
            }

            context("끄적을 읽기에 거리가 너무 먼 경우") {
                every { exceptionTestService.error() } throws LetterNotCloseEnoughException(0, 10.0)

                it("LETTER_NOT_CLOSE_ENOUGH") {
                    mockMvc.perform(get("/error"))
                        .andDo(document(
                            "error/0200",
                            getDocumentResponse()))
                        .andExpect(status().isBadRequest)
                        .andExpect(jsonPath("$.errorCode", `is`(200)))
                }
            }

            context("끄적 조회 기간이 지난 경우") {
                every { exceptionTestService.error() } throws LetterViewableTimeExpiredException()

                it("LETTER_VIEWABLE_TIME_EXPIRED") {
                    mockMvc.perform(get("/error"))
                        .andDo(document(
                            "error/0201",
                            getDocumentResponse()))
                        .andExpect(status().isBadRequest)
                        .andExpect(jsonPath("$.errorCode", `is`(201)))
                }
            }
        }

        this.describe("Unauthorized") {
            context("토큰이 없는 경우") {
                every { exceptionTestService.error() } throws NoTokenException()

                it("NO_TOKEN") {
                    mockMvc.perform(get("/error"))
                        .andDo(document(
                            "error/1000",
                            getDocumentResponse()))
                        .andExpect(status().isUnauthorized)
                        .andExpect(jsonPath("$.errorCode", `is`(1000)))
                }
            }

            context("로그인에 실패한 경우") {
                every { exceptionTestService.error() } throws LoginFailedException()

                it("LOGIN_FAILED") {
                    mockMvc.perform(get("/error"))
                        .andDo(document(
                            "error/1001",
                            getDocumentResponse()))
                        .andExpect(status().isUnauthorized)
                        .andExpect(jsonPath("$.errorCode", `is`(1001)))
                }
            }
        }

        this.describe("Forbidden") {
            context("API 접근 권한이 없는 경우") {
                every { exceptionTestService.error() } throws WrongAPIException()

                it("WRONG_API") {
                    mockMvc.perform(get("/error"))
                        .andDo(document(
                            "error/3000",
                            getDocumentResponse()))
                        .andExpect(status().isForbidden)
                        .andExpect(jsonPath("$.errorCode", `is`(3000)))
                }
            }

            context("끄적을 삭제할 권한이 없는 경우") {
                every { exceptionTestService.error() } throws LetterDeleteException()

                it("LETTER_DELETE_FORBIDDEN") {
                    mockMvc.perform(get("/error"))
                        .andDo(document(
                            "error/3100",
                            getDocumentResponse()))
                        .andExpect(status().isForbidden)
                        .andExpect(jsonPath("$.errorCode", `is`(3100)))
                }
            }
        }

        this.describe("Not Found") {
            context("끄적을 찾지 못한 경우") {
                every { exceptionTestService.error() } throws LetterNotFoundException(1L)

                it("LETTER_NOT_FOUND") {
                    mockMvc.perform(get("/error"))
                        .andDo(document(
                            "error/4000",
                            getDocumentResponse()))
                        .andExpect(status().isNotFound)
                        .andExpect(jsonPath("$.errorCode", `is`(4000)))
                }
            }

            context("유저를 찾지 못한 경우") {
                every { exceptionTestService.error() } throws UserNotFoundException()

                it("USER_NOT_FOUND") {
                    mockMvc.perform(get("/error"))
                        .andDo(document(
                            "error/4100",
                            getDocumentResponse()))
                        .andExpect(status().isNotFound)
                        .andExpect(jsonPath("$.errorCode", `is`(4100)))
                }
            }
        }

        this.describe("Conflict") {
            context("끄적 ID가 겹치는 경우") {
                every { exceptionTestService.error() } throws DuplicateGgzzIdException("ggzzId")

                it("DUPLICATE_ID") {
                    mockMvc.perform(get("/error"))
                        .andDo(document(
                            "error/9000",
                            getDocumentResponse()))
                        .andExpect(status().isConflict)
                        .andExpect(jsonPath("$.errorCode", `is`(9000)))
                }
            }

            context("username이 겹치는 경우") {
                every { exceptionTestService.error() } throws DuplicateUsernameException("username")

                it("DUPLICATE_USERNAME") {
                    mockMvc.perform(get("/error"))
                        .andDo(document(
                            "error/9001",
                            getDocumentResponse()))
                        .andExpect(status().isConflict)
                        .andExpect(jsonPath("$.errorCode", `is`(9001)))
                }
            }

            context("이미 좋아요한 끄적에 좋아요하는 경우") {
                every { exceptionTestService.error() } throws LikeAlreadyExistsException(1L, 1L)

                it("ALREADY_LIKED_LETTER") {
                    mockMvc.perform(get("/error"))
                        .andDo(document(
                            "error/9100",
                            getDocumentResponse()))
                        .andExpect(status().isConflict)
                        .andExpect(jsonPath("$.errorCode", `is`(9100)))
                }
            }
        }

        this.describe("Server Error") {
            context("예상하지 못한 예외가 발생한 경우") {
                every { exceptionTestService.error() } throws InternalServerError("unexpected error")

                it("INTERNAL_SERVER_ERROR") {
                    mockMvc.perform(get("/error"))
                        .andDo(document(
                            "error/10000",
                            getDocumentResponse()))
                        .andExpect(status().isInternalServerError)
                        .andExpect(jsonPath("$.errorCode", `is`(10000)))
                }
            }

            context("파일 업로드에 실패한 경우") {
                every { exceptionTestService.error() } throws FileUploadFailException()

                it("FILE_UPLOAD_FAILED") {
                    mockMvc.perform(get("/error"))
                        .andDo(document(
                            "error/10001",
                            getDocumentResponse()))
                        .andExpect(status().isInternalServerError)
                        .andExpect(jsonPath("$.errorCode", `is`(10001)))
                }
            }

            context("파일 삭제에 실패한 경우") {
                every { exceptionTestService.error() } throws FileDeleteFailException()

                it("FILE_DELETE_FAILED") {
                    mockMvc.perform(get("/error"))
                        .andDo(document(
                            "error/10002",
                            getDocumentResponse()))
                        .andExpect(status().isInternalServerError)
                        .andExpect(jsonPath("$.errorCode", `is`(10002)))
                }
            }
        }
    }

}