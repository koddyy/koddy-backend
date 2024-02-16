package com.koddy.server.common.mock.fake;

import com.koddy.server.global.utils.encrypt.Encryptor;

public class FakeEncryptor implements Encryptor {
    private static final String DUMMY = "_koddy";

    @Override
    public String hash(final String value) {
        return value + DUMMY;
    }

    @Override
    public boolean matches(final String rawValue, final String encodedValue) {
        return encodedValue.replace(DUMMY, "").equals(rawValue);
    }

    @Override
    public String encrypt(final String value) {
        return value + DUMMY;
    }

    @Override
    public String decrypt(final String value) {
        return value.replace(DUMMY, "");
    }
}
