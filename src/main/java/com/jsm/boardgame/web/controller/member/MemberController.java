package com.jsm.boardgame.web.controller.member;

import com.jsm.boardgame.config.security.LoginMember;
import com.jsm.boardgame.service.member.MemberService;
import com.jsm.boardgame.web.dto.request.member.*;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/v1/member")
public class MemberController {

    private final MemberService memberService;

    @PostMapping
    public ResponseEntity<Void> createMember(@Valid @RequestBody CreateMemberRequestDto requestDto) {
        memberService.createMember(requestDto);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/exists/username")
    public ResponseEntity<Boolean> existsNickname(@Valid @RequestBody ExistsUsernameRequestDto requestDto) {
        boolean isExist = memberService.existsUsername(requestDto.getUsername());
        return ResponseEntity.ok(isExist);
    }

    @GetMapping("/exists/nickname")
    public ResponseEntity<Boolean> existsUsername(@Valid @RequestBody ExistsNicknameRequestDto requestDto) {
        boolean isExist = memberService.existsNickname(requestDto.getNickname());
        return ResponseEntity.ok(isExist);
    }

    @PutMapping("/nickname")
    public ResponseEntity<Void> updateNickname(@LoginMember Long memberId, @Valid @RequestBody UpdateNicknameRequestDto requestDto) {
        memberService.updateNickname(memberId, requestDto);
        return ResponseEntity.ok().build();
    }

    @PutMapping("/password")
    public ResponseEntity<Void> updatePassword(@LoginMember Long memberId, @Valid @RequestBody UpdatePasswordRequestDto requestDto) {
        memberService.updatePassword(memberId, requestDto);
        return ResponseEntity.ok().build();
    }

    @PutMapping("/profile")
    public ResponseEntity<Void> updateProfile(@LoginMember Long memberId, @Valid @RequestBody UpdateProfileRequestDto requestDto) {
        memberService.updateProfile(memberId, requestDto);
        return ResponseEntity.ok().build();
    }
}