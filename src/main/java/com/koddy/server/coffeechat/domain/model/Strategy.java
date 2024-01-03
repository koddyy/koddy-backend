package com.koddy.server.coffeechat.domain.model;

import com.koddy.server.global.encrypt.Encryptor;
import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Lob;
import lombok.Getter;
import lombok.NoArgsConstructor;

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

    public enum Type {
        LINK,
        KAKAO_ID,
        LINK_ID,
        WECHAT_ID
    }
}
