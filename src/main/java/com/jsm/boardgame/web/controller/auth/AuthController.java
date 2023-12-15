package com.jsm.boardgame.web.controller.auth;

import com.jsm.boardgame.service.auth.AuthService;
import com.jsm.boardgame.web.dto.request.auth.LoginRequestDto;
import com.jsm.boardgame.web.dto.request.auth.ReissueRequestDto;
import com.jsm.boardgame.web.dto.response.auth.LoginTokenResponseDto;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

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

    @PutMapping("/reissue")
    public ResponseEntity<LoginTokenResponseDto> reissue(@Valid @RequestBody ReissueRequestDto requestDto) {
        LoginTokenResponseDto responseDto = authService.reissue(requestDto);
        return ResponseEntity.ok(responseDto);
    }
}
