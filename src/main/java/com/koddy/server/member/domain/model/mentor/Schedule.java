package com.koddy.server.member.domain.model.mentor;

import jakarta.persistence.Embedded;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

import static jakarta.persistence.FetchType.LAZY;
import static jakarta.persistence.GenerationType.IDENTITY;

@Entity
@Table(name = "mentor_schedule")
public class Schedule {
    protected Schedule() {
    }

    @Id
    @GeneratedValue(strategy = IDENTITY)
    private Long id;

    @Embedded
    private Timeline timeline;

    @ManyToOne(fetch = LAZY, optional = false)
    @JoinColumn(name = "mentor_id", referencedColumnName = "id", nullable = false)
    private Mentor mentor;

    public Schedule(final Mentor mentor, final Timeline timeline) {
        this.mentor = mentor;
        this.timeline = timeline;
    }

    public Long getId() {
        return id;
    }

    public Timeline getTimeline() {
        return timeline;
    }

    public Mentor getMentor() {
        return mentor;
    }
}
