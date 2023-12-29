package com.koddy.server.auth.domain.model.code;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum AuthKey {
    EMAIL("EMAIL:%s"),
    ;

    private final String value;

    public String generateAuthKey(final String suffix) {
        return String.format(this.value, suffix);
    }
}
