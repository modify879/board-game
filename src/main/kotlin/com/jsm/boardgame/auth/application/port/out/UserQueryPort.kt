package com.jsm.boardgame.auth.application.port.out

/**
 * auth 컨텍스트가 user 컨텍스트의 정보를 요구하는 포트
 * auth가 필요로 하는 인터페이스를 정의
 * user 컨텍스트가 이 인터페이스를 구현
 */
interface UserQueryPort {
    fun findByUsername(username: String): UserQueryResult?
    fun findById(userId: Long): UserQueryResult?
    fun verifyPassword(username: String, rawPassword: String): Boolean
}

/**
 * user 컨텍스트로부터 받는 사용자 정보 DTO
 */
data class UserQueryResult(
    val userId: Long,
    val username: String,
    val role: String,
)
