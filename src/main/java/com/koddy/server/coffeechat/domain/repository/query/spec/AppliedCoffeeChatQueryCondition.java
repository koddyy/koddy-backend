//package com.koddy.server.coffeechat.domain.repository.query.spec;
//
//import com.koddy.server.coffeechat.domain.model.CoffeeChatStatus;
//import com.koddy.server.coffeechat.exception.CoffeeChatException;
//import org.springframework.util.CollectionUtils;
//
//import java.util.List;
//
//import static com.koddy.server.coffeechat.exception.CoffeeChatExceptionCode.INVALID_COFFEECHAT_STATUS;
//
//public record AppliedCoffeeChatQueryCondition(
//        long memberId,
//        List<CoffeeChatStatus> status
//) {
//    public AppliedCoffeeChatQueryCondition {
//        if (!CollectionUtils.isEmpty(status)) {
//            final boolean mentorFlowExists = status.stream()
//                    .anyMatch(CoffeeChatStatus::isMentorFlow);
//
//            if (mentorFlowExists) {
//                throw new CoffeeChatException(INVALID_COFFEECHAT_STATUS);
//            }
//        }
//    }
//}