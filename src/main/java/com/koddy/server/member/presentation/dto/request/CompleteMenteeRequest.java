package com.koddy.server.member.presentation.dto.request;

import com.koddy.server.member.domain.model.Language;
import com.koddy.server.member.domain.model.Nationality;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

import java.util.List;

public record CompleteMenteeRequest(
        @NotBlank(message = "이름은 필수입니다.")
        String name,

        @NotNull(message = "국적은 필수입니다.")
        Nationality nationality,

        String profileUploadUrl,

        @NotEmpty(message = "사용 가능한 언어는 하나 이상 선택해야 합니다.")
        List<Language> languages,

        @NotBlank(message = "관심있는 학교는 필수입니다.")
        String interestSchool,

        @NotBlank(message = "관심있는 전공은 필수입니다.")
        String interestMajor
) {
}

//@Getter
//@Setter
//@NoArgsConstructor
//@AllArgsConstructor
//@ToString
//public class CompleteMenteeRequest {
//    @NotBlank(message = "이름은 필수입니다.")
//    String name,
//
//    @NotNull(message = "국적은 필수입니다.")
//    Nationality nationality,
//
//    String profileUploadUrl,
//
//    @NotEmpty(message = "사용 가능한 언어는 하나 이상 선택해야 합니다.")
//    List<Language> languages,
//
//    @NotBlank(message = "관심있는 학교는 필수입니다.")
//    String interestSchool,
//
//    @NotBlank(message = "관심있는 전공은 필수입니다.")
//    String interestMajor,
//}