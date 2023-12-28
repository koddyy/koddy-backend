package com.koddy.server.member.domain.model;

import com.koddy.server.global.encrypt.Encryptor;
import com.koddy.server.member.exception.MemberException;
import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.regex.Pattern;

import static com.koddy.server.member.exception.MemberExceptionCode.INVALID_PASSWORD_PATTERN;
import static com.koddy.server.member.exception.MemberExceptionCode.PASSWORD_SAME_AS_BEFORE;
import static lombok.AccessLevel.PROTECTED;

@Getter
@NoArgsConstructor(access = PROTECTED)
@Embeddable
public class Password {
    /**
     * 알파벳 + 숫자 + 특수기호(!@#$%^&*) + 10자 이상 20자 이하
     */
    private static final Pattern pattern = Pattern.compile("^(?=.*[a-zA-Z])(?=.*[!@#$%^&*])(?=.*[0-9])(?!.*[^A-Za-z0-9!@#$%^&*]).{10,20}$");

    @Column(name = "password", nullable = false)
    private String value;

    private Password(final String value) {
        this.value = value;
    }

    public static Password encrypt(final String value, final Encryptor encryptor) {
        validatePasswordPattern(value);
        return new Password(encryptor.hashEncrypt(value));
    }

    public Password update(final String value, final Encryptor encryptor) {
        validatePasswordPattern(value);
        validatePasswordSameAsBefore(value, encryptor);
        return new Password(encryptor.hashEncrypt(value));
    }

    private static void validatePasswordPattern(final String value) {
        if (isInvalidPattern(value)) {
            throw new MemberException(INVALID_PASSWORD_PATTERN);
        }
    }

    private static boolean isInvalidPattern(final String value) {
        return !pattern.matcher(value).matches();
    }

    private void validatePasswordSameAsBefore(final String value, final Encryptor encryptor) {
        if (encryptor.isHashMatch(value, this.value)) {
            throw new MemberException(PASSWORD_SAME_AS_BEFORE);
        }
    }
}
