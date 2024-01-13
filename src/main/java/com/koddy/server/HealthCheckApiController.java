package com.koddy.server;

import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "헬스 체크 API")
@RestController
public class HealthCheckApiController {
    @GetMapping("/api/health")
    public ResponseEntity<Void> hello() {
        return ResponseEntity.ok().build();
    }
}
