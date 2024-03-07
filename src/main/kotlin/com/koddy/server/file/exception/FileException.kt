package com.koddy.server.file.exception

import com.koddy.server.global.base.BusinessException

class FileException(
    override val code: FileExceptionCode,
) : BusinessException(code)
