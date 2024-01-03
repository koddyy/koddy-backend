package com.koddy.server.common.mock;

import com.koddy.server.global.encrypt.Encryptor;

public class FakeEncryptor implements Encryptor {
    private static final String DUMMY = "_koddy";

    @Override
    public String hashEncrypt(final String value) {
        return value + DUMMY;
    }

    @Override
    public boolean isHashMatch(final String rawValue, final String encodedValue) {
        return encodedValue.replace(DUMMY, "").equals(rawValue);
    }

    @Override
    public String symmetricEncrypt(final String value) {
        return value + DUMMY;
    }

    @Override
    public String symmetricDecrypt(final String value) {
        return value.replace(DUMMY, "");
    }
}
