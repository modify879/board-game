package com.jsm.boardgame.web.controller.member;

import com.jsm.boardgame.service.member.MemberService;
import com.jsm.boardgame.web.dto.request.member.CreateMemberRequest;
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
    public ResponseEntity<Void> createMember(@Valid @RequestBody CreateMemberRequest request) {
        memberService.createMember(request);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/exists/username")
    public ResponseEntity<Boolean> existsNickname(@RequestParam String username) {
        boolean isExist = memberService.existsUsername(username);
        return ResponseEntity.ok(isExist);
    }

    @GetMapping("/exists/nickname")
    public ResponseEntity<Boolean> existsUsername(@RequestParam String nickname) {
        boolean isExist = memberService.existsNickname(nickname);
        return ResponseEntity.ok(isExist);
    }
}