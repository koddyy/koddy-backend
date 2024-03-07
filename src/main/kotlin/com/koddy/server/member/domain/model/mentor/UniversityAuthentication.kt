package com.koddy.server.member.domain.model.mentor

import com.koddy.server.member.domain.model.mentor.UniversityAuthentication.AuthenticationStatus.ATTEMPT
import com.koddy.server.member.domain.model.mentor.UniversityAuthentication.AuthenticationStatus.SUCCESS
import jakarta.persistence.Column
import jakarta.persistence.Embeddable
import jakarta.persistence.EnumType.STRING
import jakarta.persistence.Enumerated

@Embeddable
data class UniversityAuthentication(
    @Column(name = "school_mail")
    val schoolMail: String?,

    @Column(name = "proof_data_upload_url")
    val proofDataUploadUrl: String?,

    @Enumerated(STRING)
    @Column(name = "auth_status", columnDefinition = "VARCHAR(20)")
    val status: AuthenticationStatus,
) {
    val isAuthenticated: Boolean
        get() = status == SUCCESS

    fun complete(): UniversityAuthentication {
        return UniversityAuthentication(schoolMail, proofDataUploadUrl, SUCCESS)
    }

    companion object {
        fun attemptMail(schoolMail: String): UniversityAuthentication {
            return UniversityAuthentication(
                schoolMail = schoolMail,
                proofDataUploadUrl = null,
                status = ATTEMPT,
            )
        }

        fun attemptProofData(proofDataUploadUrl: String): UniversityAuthentication {
            return UniversityAuthentication(
                schoolMail = null,
                proofDataUploadUrl = proofDataUploadUrl,
                status = ATTEMPT,
            )
        }
    }

    enum class AuthenticationStatus {
        ATTEMPT,
        SUCCESS,
    }
}
