package com.koddy.server.common.utils;

import com.koddy.server.common.mock.fake.FakeEncryptor;
import com.koddy.server.global.encrypt.Encryptor;

public class EncryptorFactory {
    private static final Encryptor ENCRYPTOR = new FakeEncryptor();

    public static Encryptor getEncryptor() {
        return ENCRYPTOR;
    }
}
