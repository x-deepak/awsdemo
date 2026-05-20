package com.avis.awscloud.controller;

import com.avis.awscloud.dto.LambdaRequest;
import com.avis.awscloud.dto.LambdaResponse;
import com.avis.awscloud.service.LambdaInvokeService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/lambda")
public class LambdaController {

    private final LambdaInvokeService lambdaInvokeService;

    public LambdaController(LambdaInvokeService lambdaInvokeService) {
        this.lambdaInvokeService = lambdaInvokeService;
    }

    @PostMapping("/double")
    public ResponseEntity<?> doubleNumber(@RequestBody LambdaRequest req) {
        try {
            int result = lambdaInvokeService.doubleNumber(req.getNumber());
            return ResponseEntity.ok(new LambdaResponse(result));
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body(Map.of("error", e.getMessage()));
        }
    }
}
