package com.wafflestudio.nostalgia.global.common.model

import jakarta.persistence.*
import org.springframework.data.annotation.CreatedDate
import org.springframework.data.annotation.LastModifiedDate
import org.springframework.data.jpa.domain.support.AuditingEntityListener
import java.time.LocalDateTime

@MappedSuperclass
@EntityListeners(AuditingEntityListener::class)
open class BaseTimeTraceEntity(
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    open val id: Long = 0,

    @CreatedDate
    open var createdAt: LocalDateTime = LocalDateTime.now(),

    @LastModifiedDate
    open var modifiedAt: LocalDateTime = LocalDateTime.now(),
)