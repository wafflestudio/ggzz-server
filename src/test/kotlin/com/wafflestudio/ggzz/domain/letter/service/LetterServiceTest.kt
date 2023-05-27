package com.wafflestudio.ggzz.domain.letter.service


import com.amazonaws.services.s3.AmazonS3
import com.wafflestudio.ggzz.domain.letter.exception.LetterViewableTimeExpiredException
import com.wafflestudio.ggzz.domain.letter.model.Letter
import com.wafflestudio.ggzz.domain.letter.repository.LetterRepository
import com.wafflestudio.ggzz.domain.user.model.User
import com.wafflestudio.ggzz.domain.user.repository.UserRepository
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.IsolationMode
import io.kotest.core.spec.style.BehaviorSpec
import io.kotest.matchers.ints.shouldBeExactly
import io.kotest.matchers.shouldBe
import io.kotest.matchers.string.shouldMatch
import io.mockk.every
import io.mockk.mockk
import java.time.LocalDateTime


internal class LetterServiceTest : BehaviorSpec({
    isolationMode = IsolationMode.InstancePerLeaf

    val userRepository = mockk<UserRepository>()
    val letterRepository = mockk<LetterRepository>()
    val amazonS3 = mockk<AmazonS3>()
    val letterService = LetterService(userRepository, letterRepository, amazonS3)

    given("공개 시간이 끝난 끄적과 끝나지 않은 끄적이 있을때") {

        val user = User(
            username = "Simon",
            nickname = "paul",
            password = "password"
        )
        val viewableLetter = Letter(
            user = user,
            title = "viewable",
            summary = "summary",
            longitude = 127.0,
            latitude = 37.0,
            text = null,
            image = null,
            voice = null,
            isViewable = true
        )

        val unViewableLetter = Letter(
            user = user,
            title = "unviewable",
            summary = "summary",
            longitude = 127.0,
            latitude = 37.0,
            text = null,
            image = null,
            voice = null,
            isViewable = false
        )

        every { letterRepository.findAll() } returns listOf(viewableLetter, unViewableLetter)
        every { letterRepository.findLetterById(1) } returns viewableLetter
        every { letterRepository.findLetterById(2) } returns unViewableLetter

        `when`("범위 내 끄적 전체 조회 하면") {
            val letters = letterService.getLetters(Pair(127.0, 37.0), 200)
            then("공개 시간이 끝나지 않은 끄적만 반환한다") {
                letters.count shouldBeExactly 1
                letters.data[0].title shouldMatch "viewable"
            }
        }

        `when`("공개 시간 끝난 끄적 단건 조회 하면") {
            then("LetterViewableTimeExpiredException 을 던진다") {
                shouldThrow<LetterViewableTimeExpiredException> { letterService.getLetter(2, Pair(127.0, 37.0)) }
            }
        }

        `when`("공개 시간 끝나지 않은 끄적 단건 조회 하면") {
            val letter = letterService.getLetter(1, Pair(127.0, 37.0))
            then("정상적으로 조회한다") {
                letter.title shouldMatch "viewable"
            }
        }
    }

    given("생성된지 오래된 isViewable 가 true 인 끄적이 있을때") {
        val user = User(
            username = "Simon",
            nickname = "paul",
            password = "password"
        )
        val oldLetter = Letter(
            user = user,
            title = "old",
            summary = "summary",
            longitude = 127.0,
            latitude = 37.0,
            text = null,
            image = null,
            voice = null,
            isViewable = true,
            viewableTime = 24
        )
        val noLimitLetter = Letter(
            user = user,
            title = "viewable",
            summary = "summary",
            longitude = 127.0,
            latitude = 37.0,
            text = null,
            image = null,
            voice = null,
            isViewable = true,
            viewableTime = 0
        )
        oldLetter.createdAt = LocalDateTime.MIN
        noLimitLetter.createdAt = LocalDateTime.MIN

        every { letterRepository.findLetterById(any()) } returns oldLetter
        every { letterRepository.findAllByIsViewableTrueAndViewableTimeNot(0) } returns listOf(oldLetter, noLimitLetter)

        `when`("정해진 끄적을 업데이트하면") {
            letterService.updateViewable(1)
            then("isViewable 필드가 false 로 업데이트 된다") {
                oldLetter.isViewable shouldBe false
            }
        }

        `when`("모든 끄적을 업데이트 하면") {
            letterService.updateAllViewable()
            then("공개 시간이 무제한이 아닌 끄적의 isViewable 필드만 업데이트 된다") {
                oldLetter.isViewable shouldBe false
                noLimitLetter.isViewable shouldBe true
            }
        }
    }

})
