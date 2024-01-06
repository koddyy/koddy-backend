package com.koddy.server.member.application.usecase;

import com.koddy.server.member.application.usecase.command.UpdateMenteeBasicInfoCommand;
import com.koddy.server.member.domain.model.mentee.Mentee;
import com.koddy.server.member.domain.repository.MenteeRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static com.koddy.server.common.fixture.MenteeFixture.MENTEE_1;
import static com.koddy.server.common.fixture.MenteeFixture.MENTEE_2;
import static com.koddy.server.member.domain.model.RoleType.MENTEE;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@DisplayName("Member -> UpdateMenteeInfoUseCase 테스트")
class UpdateMenteeInfoUseCaseTest {
    private final MenteeRepository menteeRepository = mock(MenteeRepository.class);
    private final UpdateMenteeInfoUseCase sut = new UpdateMenteeInfoUseCase(menteeRepository);

    @Test
    @DisplayName("Mentee 기본 정보를 수정한다")
    void updateBasicInfo() {
        // given
        final Mentee mentee = MENTEE_1.toDomain().apply(1L);
        final UpdateMenteeBasicInfoCommand command = new UpdateMenteeBasicInfoCommand(
                mentee.getId(),
                MENTEE_2.getName(),
                MENTEE_2.getNationality(),
                MENTEE_2.getProfileImageUrl(),
                MENTEE_2.getIntroduction(),
                MENTEE_2.getLanguages(),
                MENTEE_2.getInterest().getSchool(),
                MENTEE_2.getInterest().getMajor()
        );
        given(menteeRepository.getById(command.menteeId())).willReturn(mentee);

        // when
        sut.updateBasicInfo(command);

        // then
        assertAll(
                () -> verify(menteeRepository, times(1)).getById(command.menteeId()),
                () -> assertThat(mentee.getName()).isEqualTo(command.name()),
                () -> assertThat(mentee.getNationality()).isEqualTo(command.nationality()),
                () -> assertThat(mentee.getProfileImageUrl()).isEqualTo(command.profileImageUrl()),
                () -> assertThat(mentee.getIntroduction()).isEqualTo(command.introduction()),
                () -> assertThat(mentee.getLanguages()).containsExactlyInAnyOrderElementsOf(command.languages()),
                () -> assertThat(mentee.getAuthorities()).containsExactlyInAnyOrder(MENTEE.getAuthority()),
                () -> assertThat(mentee.getInterest().getSchool()).isEqualTo(command.interestSchool()),
                () -> assertThat(mentee.getInterest().getMajor()).isEqualTo(command.interestMajor())
        );
    }
}
