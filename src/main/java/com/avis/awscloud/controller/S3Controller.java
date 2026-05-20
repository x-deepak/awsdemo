package com.avis.awscloud.controller;

import com.avis.awscloud.dto.S3ObjectDto;
import com.avis.awscloud.service.S3Service;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/s3")
public class S3Controller {

    private final S3Service s3Service;

    public S3Controller(S3Service s3Service) {
        this.s3Service = s3Service;
    }

    @PostMapping("/upload")
    public ResponseEntity<Map<String, String>> upload(@RequestParam("file") MultipartFile file) throws IOException {
        String key = s3Service.upload(file);
        return ResponseEntity.ok(Map.of("key", key));
    }

    @GetMapping("/list")
    public List<S3ObjectDto> list() {
        return s3Service.listImages();
    }
}
