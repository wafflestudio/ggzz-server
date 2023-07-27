package com.wafflestudio.ggzz.domain.letter.service

import com.wafflestudio.ggzz.domain.letter.dto.LetterResponse
import com.wafflestudio.ggzz.domain.letter.exception.LetterNotFoundException
import com.wafflestudio.ggzz.domain.letter.exception.LikeAlreadyExistsException
import com.wafflestudio.ggzz.domain.letter.model.Like
import com.wafflestudio.ggzz.domain.letter.repository.LetterRepository
import com.wafflestudio.ggzz.domain.letter.repository.LikeRepository
import com.wafflestudio.ggzz.domain.user.repository.UserRepository
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
@Transactional(readOnly = true)
class LetterInteractionService(
    private val userRepository: UserRepository,
    private val letterRepository: LetterRepository,
    private val likeRepository: LikeRepository
) {
    @Transactional
    fun likeLetter(userId: Long, letterId: Long): LetterResponse {
        val user = userRepository.findUserById(userId)!!
        val letter = letterRepository.findLetterById(letterId) ?: throw LetterNotFoundException(letterId)

        likeRepository.findLikeByUserIdAndLetterId(userId, letterId)?.let {
            throw LikeAlreadyExistsException(userId, letterId)
        }

        likeRepository.save(Like(user, letter))
        letter.numberOfLikes += 1

        return LetterResponse(letter)
    }

    @Transactional
    fun unlikeLetter(userId: Long, letterId: Long): LetterResponse {
        userRepository.findUserById(userId)!!
        val letter = letterRepository.findLetterById(letterId) ?: throw LetterNotFoundException(letterId)

        likeRepository.findLikeByUserIdAndLetterId(userId, letterId)?.let {
            likeRepository.delete(it)
            letter.numberOfLikes -= 1
        }

        return LetterResponse(letter)
    }
}