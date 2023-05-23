package com.wafflestudio.ggzz.domain.letter.service

import com.amazonaws.SdkClientException
import com.amazonaws.services.s3.AmazonS3
import com.amazonaws.services.s3.model.CannedAccessControlList
import com.amazonaws.services.s3.model.DeleteObjectRequest
import com.amazonaws.services.s3.model.ObjectMetadata
import com.amazonaws.services.s3.model.PutObjectRequest
import com.wafflestudio.ggzz.domain.letter.dto.LetterDto.*
import com.wafflestudio.ggzz.domain.letter.exception.*
import com.wafflestudio.ggzz.domain.letter.model.Letter
import com.wafflestudio.ggzz.domain.letter.repository.LetterRepository
import com.wafflestudio.ggzz.domain.user.repository.UserRepository
import com.wafflestudio.ggzz.global.common.dto.ListResponse
import org.springframework.beans.factory.annotation.Value
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import org.springframework.web.multipart.MultipartFile
import java.text.SimpleDateFormat
import java.time.LocalDateTime
import java.time.temporal.ChronoUnit
import java.util.*
import kotlin.math.atan2
import kotlin.math.cos
import kotlin.math.sin
import kotlin.math.sqrt


@Service
@Transactional(readOnly = true)
class LetterService(
    private val userRepository: UserRepository,
    private val letterRepository: LetterRepository,
    private val amazonS3: AmazonS3
) {
    @Value("\${cloud.aws.s3.bucket}")
    private val bucketName: String = ""

    companion object {
        private const val EARTH_RADIUS_IN_KM = 6371
    }

    @Transactional
    fun postLetter(userId: Long, request: CreateRequest): Response {
        val user = userRepository.findMeById(userId)
        val letter = letterRepository.save(Letter(user, request))
        return Response(letter)
    }

    fun getLetters(pos: Pair<Double, Double>, range: Int): ListResponse<Response> {
        return ListResponse(letterRepository.findAll().filter { letter ->
            val letterPos = letter.longitude to letter.latitude
            distanceBetweenTwoPositionInMeter(pos, letterPos) <= range && letter.isViewable()
        }.map { Response(it) })
    }

    fun getLetter(id: Long, pos: Pair<Double, Double>): DetailResponse {
        val letter = letterRepository.findLetterById(id) ?: throw LetterNotFoundException(id)

        if (!letter.isViewable()) {
            throw LetterViewableTimeExpiredException()
        }
        val letterPos = letter.longitude to letter.latitude
        if (letter.viewRange != 0 && distanceBetweenTwoPositionInMeter(pos, letterPos) > letter.viewRange)
            throw LetterNotCloseEnoughException(letter.viewRange)
        return DetailResponse(letter)
    }

    fun getMyLetters(userId: Long): ListResponse<Response> {
        return ListResponse(letterRepository.findLettersByUserId(userId).map { Response(it) })
    }

    /* Distance */
    private fun deg2rad(deg: Double) = deg * Math.PI / 180
    private fun distanceBetweenTwoPositionInMeter(pos1: Pair<Double, Double>, pos2: Pair<Double, Double>): Double {
        val dLon = deg2rad(pos2.first - pos1.first)
        val dLat = deg2rad(pos2.second - pos1.second)

        val a = sin(dLat / 2) * sin(dLat / 2) +
                cos(deg2rad(pos1.second)) * cos(deg2rad(pos2.second)) * sin(dLon / 2) * sin(dLon / 2)
        val c = 2 * atan2(sqrt(a), sqrt(1 - a))

        return EARTH_RADIUS_IN_KM * c * 1000
    }

    @Transactional
    fun addSourceToLetter(userId: Long, id: Long, image: MultipartFile?, voice: MultipartFile?) {
        val user = userRepository.findMeById(userId)
        val letter = letterRepository.findLetterById(id) ?: throw LetterNotFoundException(id)

        image?.let {
            it.contentType ?: throw UnsupportedFileTypeException(null, "image")
            if (!it.contentType!!.contains("image")) throw UnsupportedFileTypeException(it.contentType, "image")
        }
        voice?.let {
            it.contentType ?: throw UnsupportedFileTypeException(null, "voice")
            if (!it.contentType!!.contains("audio")) throw UnsupportedFileTypeException(it.contentType, "voice")
        }

        image?.let { letter.image = uploadFile("${user.username}-image", image) }
        voice?.let { letter.voice = uploadFile("${user.username}-voice", voice) }
    }

    /* File Upload */
    private val simpleDateFormat = SimpleDateFormat("yyyyMMddHHmmssSSSS")
    private fun generateFileName(username: String) = username + "-" + simpleDateFormat.format(Date())
    private fun uploadFile(username: String, file: MultipartFile): String {
        val objectMetadata = ObjectMetadata()
        objectMetadata.contentType = file.contentType
        objectMetadata.contentLength = file.size
        val fileName: String = generateFileName(username)
        try {
            file.inputStream.use { inputStream ->
                amazonS3.putObject(
                    PutObjectRequest(bucketName, fileName, inputStream, objectMetadata).withCannedAcl(
                        CannedAccessControlList.PublicRead
                    )
                )
            }
        } catch (e: SdkClientException) {
            e.printStackTrace()
            throw FileUploadFailException()
        }
        return amazonS3.getUrl(bucketName, fileName).toString()
    }

    @Transactional
    fun deleteLetter(userId: Long, id: Long) {
        val letter = letterRepository.findLetterById(id) ?: throw LetterNotFoundException(id)
        if (letter.user.id != userId) throw LetterDeleteException()

        letter.image?.let { deleteFile(it) }
        letter.voice?.let { deleteFile(it) }

        letterRepository.delete(letter)
    }

    /* File Delete */
    private fun deleteFile(url: String) {
        val key = url.substring(url.lastIndexOf('/') + 1)
        try {
            amazonS3.deleteObject(DeleteObjectRequest(bucketName, key))
        } catch (e: SdkClientException) {
            e.printStackTrace()
            throw FileDeleteFailException()
        }
    }
}
