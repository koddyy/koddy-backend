package com.koddy.server.coffeechat.domain.model;

import com.koddy.server.coffeechat.exception.CoffeeChatException;
import com.koddy.server.global.utils.encrypt.Encryptor;
import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Lob;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;

import java.util.Arrays;

import static com.koddy.server.coffeechat.exception.CoffeeChatExceptionCode.INVALID_MEETING_STRATEGY;
import static jakarta.persistence.EnumType.STRING;
import static lombok.AccessLevel.PROTECTED;

@Getter
@NoArgsConstructor(access = PROTECTED)
@Embeddable
public class Strategy {
    @Enumerated(STRING)
    @Column(name = "chat_type", columnDefinition = "VARCHAR(30)")
    private Type type;

    @Lob
    @Column(name = "chat_type_value", columnDefinition = "TEXT")
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
        ZOOM_LINK("줌 링크", "zoom"),
        GOOGLE_MEET_LINK("구글 미트 링크", "google"),
        KAKAO_ID("카카오톡 ID", "kakao"),
        LINK_ID("라인 ID", "line"),
        WECHAT_ID("위챗 ID", "wechat"),
        ;

        private final String kor;
        private final String eng;

        public static Type from(final String eng) {
            return Arrays.stream(values())
                    .filter(it -> it.eng.equals(eng))
                    .findFirst()
                    .orElseThrow(() -> new CoffeeChatException(INVALID_MEETING_STRATEGY));
        }
    }
}
