package com.koddy.server.coffeechat.application.usecase;

import com.koddy.server.coffeechat.application.usecase.query.GetCoffeeChatScheduleDetails;
import com.koddy.server.coffeechat.application.usecase.query.response.MenteeCoffeeChatScheduleDetails;
import com.koddy.server.coffeechat.application.usecase.query.response.MentorCoffeeChatScheduleDetails;
import com.koddy.server.coffeechat.domain.model.CoffeeChat;
import com.koddy.server.coffeechat.domain.model.response.CoffeeChatScheduleDetails;
import com.koddy.server.coffeechat.domain.repository.CoffeeChatRepository;
import com.koddy.server.global.annotation.KoddyReadOnlyTransactional;
import com.koddy.server.global.annotation.UseCase;
import com.koddy.server.global.utils.encrypt.Encryptor;
import com.koddy.server.member.domain.model.Member;
import com.koddy.server.member.domain.model.mentee.Mentee;
import com.koddy.server.member.domain.model.mentor.Mentor;
import com.koddy.server.member.domain.repository.MemberRepository;
import lombok.RequiredArgsConstructor;

@UseCase
@RequiredArgsConstructor
public class GetCoffeeChatScheduleDetailsUseCase {
    private final CoffeeChatRepository coffeeChatRepository;
    private final MemberRepository memberRepository;
    private final Encryptor encryptor;

    @KoddyReadOnlyTransactional
    public CoffeeChatScheduleDetails invoke(final GetCoffeeChatScheduleDetails query) {
        final CoffeeChat coffeeChat = coffeeChatRepository.getById(query.coffeeChatId());
        final Member<?> sourceMember = memberRepository.getById(coffeeChat.getSourceMemberId());
        final Member<?> targetMember = memberRepository.getById(coffeeChat.getTargetMemberId());

        if (query.authenticated().isMentor()) {
            return convertForMentor(coffeeChat, sourceMember, targetMember);
        }
        return convertForMentee(coffeeChat, sourceMember, targetMember);
    }

    private CoffeeChatScheduleDetails convertForMentor(
            final CoffeeChat coffeeChat,
            final Member<?> sourceMember,
            final Member<?> targetMember
    ) {
        if (sourceMember instanceof Mentee) {
            return MentorCoffeeChatScheduleDetails.of((Mentee) sourceMember, coffeeChat, encryptor);
        }
        return MentorCoffeeChatScheduleDetails.of((Mentee) targetMember, coffeeChat, encryptor);
    }

    private CoffeeChatScheduleDetails convertForMentee(
            final CoffeeChat coffeeChat,
            final Member<?> sourceMember,
            final Member<?> targetMember
    ) {
        if (sourceMember instanceof Mentor) {
            return MenteeCoffeeChatScheduleDetails.of((Mentor) sourceMember, coffeeChat, encryptor);
        }
        return MenteeCoffeeChatScheduleDetails.of((Mentor) targetMember, coffeeChat, encryptor);
    }
}
