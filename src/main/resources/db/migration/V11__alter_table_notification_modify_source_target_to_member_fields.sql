ALTER TABLE notification
    CHANGE source_member_id mentor_id BIGINT NOT NULL;

ALTER TABLE notification
    CHANGE target_member_id mentee_id BIGINT NOT NULL;

ALTER TABLE notification
    ADD COLUMN target_id BIGINT NOT NULL AFTER id;

ALTER TABLE notification
    MODIFY message TEXT NOT NULL COMMENT '';

ALTER TABLE notification
    DROP FOREIGN KEY fk_notification_source_member_id_from_member;

ALTER TABLE notification
    DROP FOREIGN KEY fk_notification_target_member_id_from_member;

ALTER TABLE notification
    ADD CONSTRAINT fk_notification_target_id_from_member
        FOREIGN KEY (target_id)
            REFERENCES member (id);

ALTER TABLE notification
    ADD CONSTRAINT fk_notification_mentor_id_from_member
        FOREIGN KEY (mentor_id)
            REFERENCES member (id);

ALTER TABLE notification
    ADD CONSTRAINT fk_notification_mentee_id_from_member
        FOREIGN KEY (mentee_id)
            REFERENCES member (id);
