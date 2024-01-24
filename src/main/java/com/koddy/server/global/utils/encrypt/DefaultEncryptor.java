package com.koddy.server.global.utils.encrypt;

import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.encrypt.BytesEncryptor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.util.Base64;

import static java.nio.charset.StandardCharsets.UTF_8;

@Component
@RequiredArgsConstructor
public class DefaultEncryptor implements Encryptor {
    private final PasswordEncoder passwordEncoder;
    private final BytesEncryptor bytesEncryptor;

    @Override
    public String hashEncrypt(final String value) {
        return passwordEncoder.encode(value);
    }

    @Override
    public boolean isHashMatch(final String rawValue, final String encodedValue) {
        return passwordEncoder.matches(rawValue, encodedValue);
    }

    @Override
    public String symmetricEncrypt(final String value) {
        final byte[] encryptedBytes = bytesEncryptor.encrypt(value.getBytes(UTF_8));
        return Base64.getEncoder().encodeToString(encryptedBytes);
    }

    @Override
    public String symmetricDecrypt(final String value) {
        final byte[] decryptedBytes = bytesEncryptor.decrypt(Base64.getDecoder().decode(value));
        return new String(decryptedBytes, UTF_8);
    }
}
