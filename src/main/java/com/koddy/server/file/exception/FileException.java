package com.koddy.server.file.exception;

import com.koddy.server.global.base.KoddyException;
import com.koddy.server.global.base.KoddyExceptionCode;

public class FileException extends KoddyException {
    private final FileExceptionCode code;

    public FileException(final FileExceptionCode code) {
        super(code);
        this.code = code;
    }

    @Override
    public KoddyExceptionCode getCode() {
        return code;
    }
}
