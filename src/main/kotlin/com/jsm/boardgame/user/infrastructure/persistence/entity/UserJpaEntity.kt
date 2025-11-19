package com.jsm.boardgame.user.infrastructure.persistence.entity

import com.jsm.boardgame.common.infrastructure.persistence.BaseTimeEntity
import com.jsm.boardgame.user.domain.model.EncodedPassword
import com.jsm.boardgame.user.domain.model.User
import com.jsm.boardgame.user.domain.model.UserId
import com.jsm.boardgame.user.domain.model.UserRole
import jakarta.persistence.*

@Entity
@Table(name = "users")
class UserJpaEntity(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long? = null,

    @Column(nullable = false, unique = true, length = 30)
    val username: String,

    @Column(nullable = false, length = 30)
    val nickname: String,

    @Column(nullable = false)
    val password: String,

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    val role: UserRole,

    @Column(name = "profile", length = 255)
    val profile: String?,
) : BaseTimeEntity() {

    fun toDomain(): User = User(
        id = this.id?.let(UserId::of),
        username = this.username,
        nickname = this.nickname,
        password = EncodedPassword.from(this.password),
        role = this.role,
        profile = this.profile,
    )

    companion object {
        fun from(domain: User): UserJpaEntity = UserJpaEntity(
            id = domain.id?.value,
            username = domain.username,
            nickname = domain.nickname,
            password = domain.password.value(),
            role = domain.role,
            profile = domain.profile,
        )
    }
}

