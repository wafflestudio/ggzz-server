package com.wafflestudio.ggzz.domain.letter.service


import com.amazonaws.services.s3.AmazonS3
import com.wafflestudio.ggzz.domain.letter.exception.LetterNotCloseEnoughException
import com.wafflestudio.ggzz.domain.letter.exception.LetterViewableTimeExpiredException
import com.wafflestudio.ggzz.domain.letter.model.Letter
import com.wafflestudio.ggzz.domain.letter.repository.LetterRepository
import com.wafflestudio.ggzz.domain.user.model.User
import com.wafflestudio.ggzz.domain.user.repository.UserRepository
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.IsolationMode
import io.kotest.core.spec.style.BehaviorSpec
import io.kotest.matchers.ints.shouldBeExactly
import io.kotest.matchers.string.shouldMatch
import io.mockk.every
import io.mockk.mockk


internal class LetterServiceTest : BehaviorSpec({
    isolationMode = IsolationMode.InstancePerLeaf

    val userRepository = mockk<UserRepository>()
    val letterRepository = mockk<LetterRepository>()
    val amazonS3 = mockk<AmazonS3>()
    val letterService = LetterService(userRepository, letterRepository, amazonS3)

    given("공개 시간이 끝난 끄적과 끝나지 않은 끄적이 있을때") {

        val user = User(
            firebaseId = "fbId",
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
            viewableTime = 0 // 공개 시간 무제한
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
            viewableTime = -1 // 공개 시간 X
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

    given("공개 범위보다 가까운 끄적과 먼 끄적이 있을때") {
        val user = User(
            firebaseId = "fbId",
            username = "Simon",
            nickname = "paul",
            password = "password"
        )
        val nearLetter = Letter(
            user = user,
            title = "near",
            summary = "summary",
            longitude = 127.0,
            latitude = 37.0,
            text = null,
            image = null,
            voice = null,
            viewRange = 0 // 공개범위 무제한
        )

        val farLetter = Letter(
            user = user,
            title = "far",
            summary = "summary",
            longitude = 127.0,
            latitude = 37.0,
            text = null,
            image = null,
            voice = null,
            viewRange = 1 // 공개범위 1m
        )

        every { letterRepository.findLetterById(1) } returns nearLetter
        every { letterRepository.findLetterById(2) } returns farLetter

        `when`("가까운 끄적 조회하면") {
            val letter = letterService.getLetter(1, Pair(0.0, 0.0))
            then("정상적으로 조회한다") {
                letter.title shouldMatch "near"
            }
        }
        `when`("먼 끄적 조회하면") {
            then("LetterNotCloseEnoughException 을 던진다") {
                shouldThrow<LetterNotCloseEnoughException> { letterService.getLetter(2, Pair(0.0, 0.0)) }
            }
        }
    }

})
