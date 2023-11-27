package com.jsm.boardgame.web.controller.auth;

import com.jsm.boardgame.service.auth.AuthService;
import com.jsm.boardgame.web.dto.request.auth.LoginRequestDto;
import com.jsm.boardgame.web.dto.response.auth.LoginTokenResponseDto;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/v1/auth")
public class AuthController {

    private final AuthService authService;

    @PostMapping("/login")
    public ResponseEntity<LoginTokenResponseDto> login(@Valid @RequestBody LoginRequestDto requestDto) {
        LoginTokenResponseDto responseDto = authService.login(requestDto);
        return ResponseEntity.ok(responseDto);
    }
}
