package com.koddy.server.member.presentation;

import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "사용자 회원가입 관련 API")
@RestController
@RequestMapping("/api/members")
@RequiredArgsConstructor
public class SignUpApiController {
    // TODO 멘토, 멘티 회원가입으로 수정
}
