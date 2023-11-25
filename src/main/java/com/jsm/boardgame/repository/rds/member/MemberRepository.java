package com.jsm.boardgame.repository.rds.member;

import com.jsm.boardgame.entity.rds.member.Member;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MemberRepository extends JpaRepository<Member, Long> {

    boolean existsByUsername(String username);

    boolean existsByNickname(String nickname);
}
