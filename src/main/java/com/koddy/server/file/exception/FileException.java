package com.koddy.server.file.exception;

import com.koddy.server.global.base.BaseException;
import com.koddy.server.global.base.BaseExceptionCode;

public class FileException extends BaseException {
    private final FileExceptionCode code;

    public FileException(final FileExceptionCode code) {
        super(code);
        this.code = code;
    }

    @Override
    public BaseExceptionCode getCode() {
        return code;
    }
}
