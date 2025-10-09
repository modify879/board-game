package com.jsm.boardgame.user.infrastructure.jpa.entity

import com.jsm.boardgame.common.infrastructure.jpa.entity.BaseEntity
import com.jsm.boardgame.user.domain.model.User
import jakarta.persistence.*

@Entity
@Table(name = "users")
class UserEntity(

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id: Long? = null,

    @Column(unique = true)
    var username: String,

    var password: String,

    var nickname: String,

    @Enumerated(EnumType.STRING)
    var role: List<Role> = listOf(Role.USER),

    var profile: String? = null,
) : BaseEntity() {

    fun toDomain(): User = User(
        id = this.id,
        username = this.username,
        password = this.password,
        nickname = this.nickname,
        role = this.role,
        profile = this.profile,
    )

    companion object {
        fun from(user: User): UserEntity = UserEntity(
            id = user.id,
            username = user.username,
            password = user.password,
            nickname = user.nickname,
            role = user.role,
            profile = user.profile,
        )
    }
}

enum class Role { USER, ADMIN }