package com.wafflestudio.ggzz.domain.letter.service

import com.wafflestudio.ggzz.domain.letter.exception.LetterNotFoundException
import com.wafflestudio.ggzz.domain.letter.exception.LikeAlreadyExistsException
import com.wafflestudio.ggzz.domain.letter.model.Letter
import com.wafflestudio.ggzz.domain.letter.model.Like
import com.wafflestudio.ggzz.domain.letter.repository.LetterRepository
import com.wafflestudio.ggzz.domain.letter.repository.LikeRepository
import com.wafflestudio.ggzz.domain.user.exception.UserNotFoundException
import com.wafflestudio.ggzz.domain.user.model.User
import com.wafflestudio.ggzz.domain.user.repository.UserRepository
import com.wafflestudio.ggzz.global.common.exception.ErrorType.Conflict.LIKE_ALREADY_EXISTS
import com.wafflestudio.ggzz.global.common.exception.ErrorType.NotFound.LETTER_NOT_FOUND
import com.wafflestudio.ggzz.global.common.exception.ErrorType.NotFound.USER_NOT_FOUND
import io.kotest.assertions.throwables.shouldNotThrowAny
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.annotation.DisplayName
import io.kotest.core.spec.Spec
import io.kotest.core.spec.style.BehaviorSpec
import io.kotest.matchers.shouldBe
import io.mockk.*

@DisplayName("LetterInteractionService 테스트")
class LetterInteractionServiceTest : BehaviorSpec() {

    companion object {
        private val userRepository = mockk<UserRepository>()
        private val letterRepository = mockk<LetterRepository>()
        private val likeRepository = mockk<LikeRepository>()
        private val letterInteractionService = LetterInteractionService(userRepository, letterRepository, likeRepository)

        private const val USER_ID = 1L
        private const val NON_EXISTING_USER_ID = 2L

        private const val LETTER_ID = 1L
        private const val NON_EXISTING_LETTER_ID = 2L
        private const val ALREADY_LIKED_LETTER_ID = 3L

        private val user = User("username", "nickname", "password")
        private val letter = Letter(user, "title", "summary", 0.0, 0.0, "text", "image", "voice")
        private val likedLetter = Letter(user, "title", "summary", 0.0, 0.0, "text", "image", "voice", 1)
        private val like = Like(user, letter)

        private val likeSlot = slot<Like>()
    }

    override suspend fun beforeSpec(spec: Spec) {
        every { userRepository.findUserById(USER_ID) } returns user
        every { userRepository.findUserById(NON_EXISTING_USER_ID) } returns null

        every { letterRepository.findLetterById(LETTER_ID) } returns letter
        every { letterRepository.findLetterById(NON_EXISTING_LETTER_ID) } returns null
        every { letterRepository.findLetterById(ALREADY_LIKED_LETTER_ID) } returns likedLetter

        every { likeRepository.findLikeByUserIdAndLetterId(USER_ID, LETTER_ID) } returns null
        every { likeRepository.findLikeByUserIdAndLetterId(USER_ID, ALREADY_LIKED_LETTER_ID) } returns like

        every { likeRepository.save(capture(likeSlot)) } answers { likeSlot.captured }

        justRun { likeRepository.delete(any()) }
    }

    init {
        this.Given("존재하지 않는 유저 ID로") {
            When("좋아요를 누르면") {
                Then("UserNotFoundException이 발생한다") {
                    val exception = shouldThrow<UserNotFoundException> {
                        letterInteractionService.likeLetter(NON_EXISTING_USER_ID, LETTER_ID)
                    }
                    exception.errorType shouldBe USER_NOT_FOUND
                }
            }

            When("좋아요를 취소하면") {
                Then("UserNotFoundException이 발생한다") {
                    val exception = shouldThrow<UserNotFoundException> {
                        letterInteractionService.unlikeLetter(NON_EXISTING_USER_ID, LETTER_ID)
                    }
                    exception.errorType shouldBe USER_NOT_FOUND
                }
            }
        }

        this.Given("존재하지 않는 끄적 ID로") {
            When("좋아요를 누르면") {
                Then("LetterNotFoundException이 발생한다") {
                    val exception = shouldThrow<LetterNotFoundException> {
                        letterInteractionService.likeLetter(USER_ID, NON_EXISTING_LETTER_ID)
                    }
                    exception.errorType shouldBe LETTER_NOT_FOUND
                }
            }

            When("좋아요를 취소하면") {
                Then("LetterNotFoundException이 발생한다") {
                    val exception = shouldThrow<LetterNotFoundException> {
                        letterInteractionService.unlikeLetter(USER_ID, NON_EXISTING_LETTER_ID)
                    }
                    exception.errorType shouldBe LETTER_NOT_FOUND
                }
            }
        }

        this.Given("이미 좋아요를 누른 끄적에 대해") {
            When("좋아요를 누르면") {
                Then("LikeAlreadyExistsException이 발생한다") {
                    val exception = shouldThrow<LikeAlreadyExistsException> {
                        letterInteractionService.likeLetter(USER_ID, ALREADY_LIKED_LETTER_ID)
                    }
                    exception.errorType shouldBe LIKE_ALREADY_EXISTS
                }
            }

            When("좋아요를 취소하면") {
                Then("Like 개수가 떨어진다") {
                    val beforeNumberOfLikes = likedLetter.numberOfLikes
                    letterInteractionService.unlikeLetter(USER_ID, ALREADY_LIKED_LETTER_ID)

                    likedLetter.numberOfLikes shouldBe beforeNumberOfLikes - 1
                }
            }
        }

        this.Given("좋아요를 누르지 않은 끄적에 대해") {
            When("좋아요를 누르면") {
                Then("Like 개수가 늘어난다") {
                    val beforeNumberOfLikes = letter.numberOfLikes
                    letterInteractionService.likeLetter(USER_ID, LETTER_ID)

                    letter.numberOfLikes shouldBe beforeNumberOfLikes + 1
                }
            }

            When("좋아요를 취소하면") {
                Then("아무 일도 일어나지 않는다") {
                    val beforeNumberOfLikes = letter.numberOfLikes
                    letterInteractionService.unlikeLetter(USER_ID, LETTER_ID)

                    letter.numberOfLikes shouldBe beforeNumberOfLikes
                }
            }
        }

        this.Given("자기 끄적에 대해") {
            When("좋아요를 눌러도") {
                Then("예외가 발생하지 않는다") {
                    shouldNotThrowAny { letterInteractionService.likeLetter(USER_ID, LETTER_ID) }
                }
            }
        }
    }
}
