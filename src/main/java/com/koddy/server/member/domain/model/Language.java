package com.koddy.server.member.domain.model;

import com.koddy.server.member.exception.MemberException;
import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import jakarta.persistence.Enumerated;

import java.util.Arrays;

import static com.koddy.server.member.exception.MemberExceptionCode.INVALID_LANGUAGE_CATEGORY;
import static com.koddy.server.member.exception.MemberExceptionCode.INVALID_LANGUAGE_TYPE;
import static jakarta.persistence.EnumType.STRING;

@Embeddable
public class Language {
    protected Language() {
    }

    @Enumerated(STRING)
    @Column(name = "language_category", nullable = false, columnDefinition = "VARCHAR(20)")
    private Category category;

    @Enumerated(STRING)
    @Column(name = "language_type", nullable = false, columnDefinition = "VARCHAR(20)")
    private Type type;

    public Language(final Category category, final Type type) {
        this.category = category;
        this.type = type;
    }

    public Category getCategory() {
        return category;
    }

    public Type getType() {
        return type;
    }

    public enum Category {
        KR("KR", "한국어"),
        EN("EN", "영어"),
        CN("CN", "중국어"),
        JP("JP", "일본어"),
        VN("VN", "베트남어"),
        ;

        private final String code;
        private final String value;

        Category(final String code, final String value) {
            this.code = code;
            this.value = value;
        }

        public static Category from(final String code) {
            return Arrays.stream(values())
                    .filter(it -> it.code.equalsIgnoreCase(code))
                    .findFirst()
                    .orElseThrow(() -> new MemberException(INVALID_LANGUAGE_CATEGORY));
        }

        public String getCode() {
            return code;
        }

        public String getValue() {
            return value;
        }
    }

    public enum Type {
        MAIN("메인 언어"),
        SUB("서브 언어"),
        ;

        private final String value;

        Type(final String value) {
            this.value = value;
        }

        public static Type from(final String value) {
            return Arrays.stream(values())
                    .filter(it -> it.value.equals(value))
                    .findFirst()
                    .orElseThrow(() -> new MemberException(INVALID_LANGUAGE_TYPE));
        }

        public String getValue() {
            return value;
        }
    }
}
