package com.jsm.boardgame.service.member;

import com.jsm.boardgame.entity.rds.member.Member;
import com.jsm.boardgame.exception.ApiException;
import com.jsm.boardgame.exception.ErrorCodeType;
import com.jsm.boardgame.repository.rds.member.MemberRepository;
import com.jsm.boardgame.web.dto.request.member.CreateMemberRequestDto;
import com.jsm.boardgame.web.dto.request.member.UpdateNicknameRequestDto;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Service
public class MemberService {

    private final MemberRepository memberRepository;
    private final PasswordEncoder passwordEncoder;

    @Transactional
    public void createMember(CreateMemberRequestDto request) {
        if (!request.checkPassword()) {
            throw new ApiException(ErrorCodeType.PASSWORD_NOT_CORRECT);
        }

        if (existsUsername(request.getUsername())) {
            throw new ApiException(ErrorCodeType.EXISTS_USERNAME);
        }

        if (existsNickname(request.getNickname())) {
            throw new ApiException(ErrorCodeType.EXISTS_NICKNAME);
        }

        memberRepository.save(request.toMember(passwordEncoder));
    }

    @Transactional(readOnly = true)
    public boolean existsUsername(String username) {
        return memberRepository.existsByUsername(username);
    }

    @Transactional(readOnly = true)
    public boolean existsNickname(String nickname) {
        return memberRepository.existsByNickname(nickname);
    }

    @Transactional
    public void updateNickname(Long memberId, UpdateNicknameRequestDto requestDto) {
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new ApiException(ErrorCodeType.UPDATE_MEMBER_NOT_FOUND));
        member.updateNickname(requestDto.getNickname());
    }
}
