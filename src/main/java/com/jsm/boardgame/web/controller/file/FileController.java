package com.jsm.boardgame.web.controller.file;

import com.jsm.boardgame.service.file.FileService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/v1/file")
public class FileController {

    private final FileService fileService;

    @PostMapping("/image")
    public ResponseEntity<String> uploadImage(@RequestPart MultipartFile image) throws IOException {
        String url = fileService.uploadImage(image);
        return ResponseEntity.ok(url);
    }
}
