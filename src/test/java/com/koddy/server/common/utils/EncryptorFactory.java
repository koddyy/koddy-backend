package com.koddy.server.common.utils;

import com.koddy.server.common.mock.fake.FakeEncryptor;
import com.koddy.server.global.utils.encrypt.Encryptor;

import java.security.SecureRandom;

public class EncryptorFactory {
    private static final Encryptor ENCRYPTOR = new FakeEncryptor();

    public static Encryptor getEncryptor() {
        return ENCRYPTOR;
    }

    public static void main(final String[] args) {
        password();
        salt();
    }

    private static void password() {
        final String characters = "0123456789abcdef";

        final SecureRandom random = new SecureRandom();
        final StringBuilder sb = new StringBuilder(64);

        for (int i = 0; i < 64; i++) {
            final int randomIndex = random.nextInt(characters.length());
            sb.append(characters.charAt(randomIndex));
        }

        System.out.println("Password = " + sb);
    }

    private static void salt() {
        final SecureRandom random = new SecureRandom();
        final byte[] salt = new byte[32];
        random.nextBytes(salt);

        final StringBuilder sb = new StringBuilder();
        for (final byte b : salt) {
            sb.append(String.format("%02x", b));
        }
        System.out.println("Salt = " + sb);
    }
}
