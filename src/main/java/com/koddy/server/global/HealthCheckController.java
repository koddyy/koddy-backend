package com.koddy.server.global;

import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "헬스 체크 API")
@RestController
public class HealthCheckController {
    @GetMapping("/api/hello")
    public ResponseEntity<Void> hello() {
        return ResponseEntity.ok().build();
    }
}
