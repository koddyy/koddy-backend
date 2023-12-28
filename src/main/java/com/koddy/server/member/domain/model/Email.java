package com.koddy.server.member.domain.model;

import com.koddy.server.member.exception.MemberException;
import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import jakarta.persistence.Enumerated;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.regex.Pattern;

import static com.koddy.server.member.domain.model.EmailStatus.ACTIVE;
import static com.koddy.server.member.domain.model.EmailStatus.INACTIVE;
import static com.koddy.server.member.domain.model.EmailStatus.SUSPEND;
import static com.koddy.server.member.exception.MemberExceptionCode.INVALID_EMAIL_PATTERN;
import static jakarta.persistence.EnumType.STRING;
import static lombok.AccessLevel.PROTECTED;

@Getter
@NoArgsConstructor(access = PROTECTED)
@Embeddable
public class Email {
    private static final Pattern pattern = Pattern.compile("^[a-zA-Z0-9+-\\_.]+@[a-zA-Z0-9-]+\\.[a-zA-Z0-9-.]+$");

    @Column(name = "email", nullable = false, unique = true, updatable = false)
    private String value;

    @Enumerated(STRING)
    @Column(name = "email_status", nullable = false, columnDefinition = "VARCHAR(20)")
    private EmailStatus status;

    private Email(final String value, final EmailStatus status) {
        this.value = value;
        this.status = status;
    }

    public static Email init(final String value) {
        validateEmailPattern(value);
        return new Email(value, INACTIVE);
    }

    private static void validateEmailPattern(final String value) {
        if (isNotValidPattern(value)) {
            throw new MemberException(INVALID_EMAIL_PATTERN);
        }
    }

    private static boolean isNotValidPattern(final String value) {
        return !pattern.matcher(value).matches();
    }

    public boolean isAuthenticated() {
        return status == ACTIVE;
    }

    public void activate() {
        this.status = ACTIVE;
    }

    public void suspend() {
        this.status = SUSPEND;
    }
}
