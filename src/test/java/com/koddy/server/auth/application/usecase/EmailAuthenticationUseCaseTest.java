package com.koddy.server.auth.application.usecase;

import com.koddy.server.auth.application.adapter.MailAuthenticationProcessor;
import com.koddy.server.auth.application.usecase.command.ConfirmAuthCodeCommand;
import com.koddy.server.auth.application.usecase.command.SendAuthCodeCommand;
import com.koddy.server.auth.domain.model.code.AuthKey;
import com.koddy.server.auth.exception.AuthException;
import com.koddy.server.common.UseCaseTest;
import com.koddy.server.mail.application.adapter.EmailSender;
import com.koddy.server.member.domain.model.Email;
import com.koddy.server.member.domain.model.Member;
import com.koddy.server.member.domain.model.Password;
import com.koddy.server.member.domain.model.mentor.Mentor;
import com.koddy.server.member.domain.repository.MemberRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import static com.koddy.server.auth.exception.AuthExceptionCode.INVALID_AUTH_CODE;
import static com.koddy.server.common.utils.EncryptorFactory.getEncryptor;
import static org.assertj.core.api.Assertions.assertThat;
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
    private final MailAuthenticationProcessor mailAuthenticationProcessor = mock(MailAuthenticationProcessor.class);
    private final EmailSender emailSender = mock(EmailSender.class);
    private final EmailAuthenticationUseCase sut = new EmailAuthenticationUseCase(
            memberRepository,
            mailAuthenticationProcessor,
            emailSender
    );

    private final Member<?> member = new Mentor(
            Email.init("sjiwon4491@gmail.com"),
            Password.encrypt("Koddy123!@#", getEncryptor())
    ).apply(1L);

    @Nested
    @DisplayName("인증번호 발송")
    class SendAuthCode {
        @Test
        @DisplayName("이름 + 이메일 + 로그인 아이디에 해당하는 사용자에게 비밀번호 재설정 인증번호를 발송한다")
        void success() {
            // given
            given(memberRepository.getById(member.getId())).willReturn(member);

            final String key = AuthKey.EMAIL.generateAuthKey(member.getEmail().getValue());
            final String authCode = "Koddy";
            given(mailAuthenticationProcessor.storeAuthCode(key)).willReturn(authCode);

            // when
            sut.sendAuthCode(new SendAuthCodeCommand(member.getId()));

            // then
            assertAll(
                    () -> verify(memberRepository, times(1)).getById(member.getId()),
                    () -> verify(mailAuthenticationProcessor, times(1)).storeAuthCode(key),
                    () -> verify(emailSender, times(1)).sendEmailAuthMail(member.getEmail().getValue(), authCode),
                    () -> assertThat(member.isAuthenticated()).isFalse()
            );
        }
    }

    @Nested
    @DisplayName("인증번호 확인")
    class ConfirmAuthCode {
        private final String email = member.getEmail().getValue();
        private final String key = AuthKey.EMAIL.generateAuthKey(email);
        private final String authCode = "Koddy";
        private final ConfirmAuthCodeCommand command = new ConfirmAuthCodeCommand(member.getId(), authCode);

        @Test
        @DisplayName("인증번호가 일치하지 않으면 사용자 인증에 실패한다")
        void throwExceptionByInvalidAuthCode() {
            // given
            given(memberRepository.getById(command.memberId())).willReturn(member);
            doThrow(new AuthException(INVALID_AUTH_CODE))
                    .when(mailAuthenticationProcessor)
                    .verifyAuthCode(key, command.authCode());

            // when - then
            assertThatThrownBy(() -> sut.confirmAuthCode(command))
                    .isInstanceOf(AuthException.class)
                    .hasMessage(INVALID_AUTH_CODE.getMessage());

            assertAll(
                    () -> verify(memberRepository, times(1)).getById(command.memberId()),
                    () -> verify(mailAuthenticationProcessor, times(1)).verifyAuthCode(key, command.authCode()),
                    () -> verify(mailAuthenticationProcessor, times(0)).deleteAuthCode(key)
            );
        }

        @Test
        @DisplayName("인증번호 검증에 성공한다")
        void success() {
            // given
            given(memberRepository.getById(command.memberId())).willReturn(member);
            doNothing()
                    .when(mailAuthenticationProcessor)
                    .verifyAuthCode(key, command.authCode());

            // when
            sut.confirmAuthCode(command);

            // then
            assertAll(
                    () -> verify(memberRepository, times(1)).getById(command.memberId()),
                    () -> verify(mailAuthenticationProcessor, times(1)).verifyAuthCode(key, command.authCode()),
                    () -> verify(mailAuthenticationProcessor, times(1)).deleteAuthCode(key),
                    () -> assertThat(member.isAuthenticated()).isTrue()
            );
        }
    }
}
