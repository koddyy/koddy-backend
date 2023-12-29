package com.koddy.server.auth.application.usecase;

import com.koddy.server.auth.application.adapter.AuthenticationProcessor;
import com.koddy.server.auth.application.usecase.command.ConfirmAuthCodeCommand;
import com.koddy.server.auth.application.usecase.command.SendAuthCodeCommand;
import com.koddy.server.auth.domain.model.code.AuthKey;
import com.koddy.server.auth.exception.AuthException;
import com.koddy.server.common.UseCaseTest;
import com.koddy.server.mail.application.adapter.EmailSender;
import com.koddy.server.member.domain.model.Member;
import com.koddy.server.member.domain.repository.MemberRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import static com.koddy.server.auth.exception.AuthExceptionCode.INVALID_AUTH_CODE;
import static com.koddy.server.common.fixture.MentorFixture.MENTOR_1;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@DisplayName("Auth -> EmailAuthenticationUseCase 테스트")
class EmailAuthenticationUseCaseTest extends UseCaseTest {
    private final MemberRepository memberRepository = mock(MemberRepository.class);
    private final AuthenticationProcessor authenticationProcessor = mock(AuthenticationProcessor.class);
    private final EmailSender emailSender = mock(EmailSender.class);
    private final EmailAuthenticationUseCase sut = new EmailAuthenticationUseCase(
            memberRepository,
            authenticationProcessor,
            emailSender
    );

    private final Member<?> member = MENTOR_1.toDomain().apply(1L);

    @Nested
    @DisplayName("인증번호 발송")
    class SendAuthCode {
        private final String email = member.getEmail().getValue();
        private final SendAuthCodeCommand command = new SendAuthCodeCommand(email);

        @Test
        @DisplayName("이름 + 이메일 + 로그인 아이디에 해당하는 사용자에게 비밀번호 재설정 인증번호를 발송한다")
        void success() {
            // given
            given(memberRepository.getByEmail(command.email())).willReturn(member);

            final String key = AuthKey.EMAIL.generateAuthKey(email);
            final String authCode = "Koddy";
            given(authenticationProcessor.storeAuthCode(key)).willReturn(authCode);

            // when
            sut.sendAuthCode(command);

            // then
            assertAll(
                    () -> verify(memberRepository, times(1)).getByEmail(command.email()),
                    () -> verify(authenticationProcessor, times(1)).storeAuthCode(key),
                    () -> verify(emailSender, times(1)).sendEmailAuthMail(email, authCode)
            );
        }
    }

    @Nested
    @DisplayName("인증번호 확인")
    class ConfirmAuthCode {
        private final String email = member.getEmail().getValue();
        private final String key = AuthKey.EMAIL.generateAuthKey(email);
        private final String authCode = "Koddy";
        private final ConfirmAuthCodeCommand command = new ConfirmAuthCodeCommand(email, authCode);

        @Test
        @DisplayName("인증번호가 일치하지 않으면 사용자 인증에 실패한다")
        void throwExceptionByInvalidAuthCode() {
            // given
            given(memberRepository.getByEmail(command.email())).willReturn(member);
            doThrow(new AuthException(INVALID_AUTH_CODE))
                    .when(authenticationProcessor)
                    .verifyAuthCode(key, command.authCode());

            // when - then
            assertThatThrownBy(() -> sut.confirmAuthCode(command))
                    .isInstanceOf(AuthException.class)
                    .hasMessage(INVALID_AUTH_CODE.getMessage());

            assertAll(
                    () -> verify(memberRepository, times(1)).getByEmail(command.email()),
                    () -> verify(authenticationProcessor, times(1)).verifyAuthCode(key, command.authCode()),
                    () -> verify(authenticationProcessor, times(0)).deleteAuthCode(key)
            );
        }

        @Test
        @DisplayName("인증번호 검증에 성공한다")
        void success() {
            // given
            given(memberRepository.getByEmail(command.email())).willReturn(member);
            doNothing()
                    .when(authenticationProcessor)
                    .verifyAuthCode(key, command.authCode());

            // when
            sut.confirmAuthCode(command);

            // then
            assertAll(
                    () -> verify(memberRepository, times(1)).getByEmail(command.email()),
                    () -> verify(authenticationProcessor, times(1)).verifyAuthCode(key, command.authCode()),
                    () -> verify(authenticationProcessor, times(1)).deleteAuthCode(key)
            );
        }
    }
}
