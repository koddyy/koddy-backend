package com.koddy.server.coffeechat.domain.model;

import com.koddy.server.global.encrypt.Encryptor;
import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Lob;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;

import static jakarta.persistence.EnumType.STRING;
import static lombok.AccessLevel.PROTECTED;

@Getter
@NoArgsConstructor(access = PROTECTED)
@Embeddable
public class Strategy {
    @Enumerated(STRING)
    @Column(name = "chat_type", nullable = false, columnDefinition = "VARCHAR(30)")
    private Type type;

    @Lob
    @Column(name = "chat_type_value", nullable = false, columnDefinition = "TEXT")
    private String value;

    private Strategy(final Type type, final String value) {
        this.type = type;
        this.value = value;
    }

    public static Strategy of(final Type type, final String value, final Encryptor encryptor) {
        return new Strategy(type, encryptor.symmetricEncrypt(value));
    }

    @Getter
    @RequiredArgsConstructor
    public enum Type {
        ZOOM_LINK("줌 링크"),
        GOOGLE_MEET_LINK("구글 미트 링크"),
        KAKAO_ID("카카오톡 ID"),
        LINK_ID("라인 ID"),
        WECHAT_ID("위챗 ID"),
        ;

        private final String value;
    }
}
