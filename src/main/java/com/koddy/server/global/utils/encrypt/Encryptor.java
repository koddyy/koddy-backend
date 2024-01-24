package com.koddy.server.global.utils.encrypt;

public interface Encryptor {
    String hashEncrypt(final String value);

    boolean isHashMatch(final String rawValue, final String encodedValue);

    String symmetricEncrypt(final String value);

    String symmetricDecrypt(final String value);
}
