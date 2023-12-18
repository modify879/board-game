package com.jsm.boardgame.entity.rds.member;

import com.jsm.boardgame.common.enums.AbstractEnumCodeAttributeConverter;
import com.jsm.boardgame.common.enums.EnumCodeType;
import com.jsm.boardgame.entity.rds.BaseTimeEntity;
import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;

@NoArgsConstructor
@Getter
@Entity
@Table(name = "member")
public class Member extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Long id;

    @Column(name = "username", nullable = false, length = 16)
    private String username;

    @Column(name = "password", nullable = false)
    private String password;

    @Column(name = "nickname", nullable = false, length = 16)
    private String nickname;

    @Column(name = "profile", nullable = false, length = 2083)
    private String profile;

    @Column(name = "point", nullable = false)
    private int point;

    @Convert(converter = RoleConverter.class)
    @Column(name = "role", nullable = false)
    private Role role;

    @Builder
    public Member(String username, String password, String nickname, String profile, int point, Role role) {
        this.username = username;
        this.password = password;
        this.nickname = nickname;
        this.profile = profile;
        this.point = point;
        this.role = role;
    }

    public void updateNickname(String nickname) {
        this.nickname = nickname;
    }

    public void updatePassword(String password) {
        this.password = password;
    }

    @RequiredArgsConstructor
    @Getter
    public enum Role implements EnumCodeType {

        ADMIN("관리자", "1"),
        MEMBER("일반 회원", "2");

        private final String desc;
        private final String code;
    }

    private static class RoleConverter extends AbstractEnumCodeAttributeConverter<Role> {

        private static final String ENUM_NAME = "권한";

        public RoleConverter() {
            super(false, ENUM_NAME);
        }
    }
}

