package com.wafflestudio.ggzz.domain.letter.service

import com.wafflestudio.ggzz.domain.letter.dto.LetterDto
import com.wafflestudio.ggzz.domain.letter.exception.LetterNotFoundException
import com.wafflestudio.ggzz.domain.letter.exception.LikeAlreadyExistsException
import com.wafflestudio.ggzz.domain.letter.model.Like
import com.wafflestudio.ggzz.domain.letter.repository.LetterRepository
import com.wafflestudio.ggzz.domain.letter.repository.LikeRepository
import com.wafflestudio.ggzz.domain.user.exception.UserNotFoundException
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
    fun likeLetter(userId: Long, letterId: Long): LetterDto.Response {
        val user = userRepository.findUserById(userId) ?: throw UserNotFoundException(userId)
        val letter = letterRepository.findLetterById(letterId) ?: throw LetterNotFoundException(letterId)

        likeRepository.findLikeByUserIdAndLetterId(userId, letterId)?.let {
            throw LikeAlreadyExistsException(userId, letterId)
        }

        likeRepository.save(Like(user, letter))
        letter.numberOfLikes += 1

        return LetterDto.Response(letter)
    }

    @Transactional
    fun unlikeLetter(userId: Long, letterId: Long): LetterDto.Response {
        userRepository.findUserById(userId) ?: throw UserNotFoundException(userId)
        val letter = letterRepository.findLetterById(letterId) ?: throw LetterNotFoundException(letterId)

        likeRepository.findLikeByUserIdAndLetterId(userId, letterId)?.let {
            likeRepository.delete(it)
            letter.numberOfLikes -= 1
        }

        return LetterDto.Response(letter)
    }
}