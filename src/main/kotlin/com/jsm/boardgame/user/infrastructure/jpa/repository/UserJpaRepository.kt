package com.jsm.boardgame.user.infrastructure.jpa.repository

import com.jsm.boardgame.user.infrastructure.jpa.entity.UserEntity
import org.springframework.data.jpa.repository.JpaRepository

interface UserJpaRepository : JpaRepository<UserEntity, Long> {

    fun findByUsername(username: String): UserEntity?
}