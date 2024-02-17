package com.koddy.server.common.mock.fake

import com.koddy.server.global.utils.encrypt.Encryptor

class FakeEncryptor : Encryptor {
    override fun hash(value: String): String = value + UNI_DIRECTIONAL_DUMMY

    override fun matches(
        rawValue: String,
        encodedValue: String,
    ): Boolean = encodedValue.replace(UNI_DIRECTIONAL_DUMMY, "") == rawValue

    override fun encrypt(value: String): String = value + BI_DIRECTIONAL_DUMMY

    override fun decrypt(value: String): String = value.replace(BI_DIRECTIONAL_DUMMY, "")

    companion object {
        private const val UNI_DIRECTIONAL_DUMMY = "_hello_koddy_uni"
        private const val BI_DIRECTIONAL_DUMMY = "_hello_koddy_bi"
    }
}
