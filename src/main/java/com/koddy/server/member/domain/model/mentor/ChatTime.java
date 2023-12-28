package com.koddy.server.member.domain.model.mentor;

import com.koddy.server.global.base.BaseEntity;
import jakarta.persistence.Embedded;
import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;

import static jakarta.persistence.FetchType.LAZY;
import static lombok.AccessLevel.PROTECTED;

@Getter
@NoArgsConstructor(access = PROTECTED)
@Entity
@Table(name = "mentor_chat_time")
public class ChatTime extends BaseEntity<ChatTime> {
    @Embedded
    private Schedule schedule;

    @ManyToOne(fetch = LAZY, optional = false)
    @JoinColumn(name = "mentor_id", referencedColumnName = "id", nullable = false)
    private Mentor mentor;

    public ChatTime(final Mentor mentor, final Schedule schedule) {
        this.mentor = mentor;
        this.schedule = schedule;
    }
}
