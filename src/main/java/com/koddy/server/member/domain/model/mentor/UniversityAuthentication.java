package com.koddy.server.member.domain.model.mentor;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import jakarta.persistence.Enumerated;
import lombok.Getter;
import lombok.NoArgsConstructor;

import static com.koddy.server.member.domain.model.mentor.AuthenticationStatus.ATTEMPT;
import static com.koddy.server.member.domain.model.mentor.AuthenticationStatus.SUCCESS;
import static jakarta.persistence.EnumType.STRING;
import static lombok.AccessLevel.PROTECTED;

@Getter
@NoArgsConstructor(access = PROTECTED)
@Embeddable
public class UniversityAuthentication {
    @Column(name = "school_mail")
    private String schoolMail;

    @Column(name = "proof_data_upload_url")
    private String proofDataUploadUrl;

    @Enumerated(STRING)
    @Column(name = "auth_status", columnDefinition = "VARCHAR(20)")
    private AuthenticationStatus status;

    private UniversityAuthentication(
            final String schoolMail,
            final String proofDataUploadUrl,
            final AuthenticationStatus status
    ) {
        this.schoolMail = schoolMail;
        this.proofDataUploadUrl = proofDataUploadUrl;
        this.status = status;
    }

    public static UniversityAuthentication attemptMail(final String schoolMail) {
        return new UniversityAuthentication(schoolMail, null, ATTEMPT);
    }

    public static UniversityAuthentication attemptProofData(final String proofDataUploadUrl) {
        return new UniversityAuthentication(null, proofDataUploadUrl, ATTEMPT);
    }

    public void complete() {
        status = SUCCESS;
    }

    public boolean isAuthenticated() {
        return status == SUCCESS;
    }
}
