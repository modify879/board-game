package com.jsm.boardgame.user.infrastructure.persistence.repository

import com.jsm.boardgame.user.infrastructure.persistence.entity.UserJpaEntity
import org.springframework.data.jpa.repository.JpaRepository

interface UserJpaRepository : JpaRepository<UserJpaEntity, Long> {
    fun existsByUsername(username: String): Boolean
    fun findByUsername(username: String): UserJpaEntity?
}

