ALTER TABLE notification
    DROP FOREIGN KEY fk_notification_source_member_id_from_member;

ALTER TABLE notification
    DROP FOREIGN KEY fk_notification_target_member_id_from_member;

ALTER TABLE notification
    DROP COLUMN source_member_id;

ALTER TABLE notification
    DROP COLUMN target_member_id;

ALTER TABLE notification
    MODIFY message TEXT NOT NULL COMMENT '';

ALTER TABLE notification
    ADD COLUMN target_id BIGINT NOT NULL AFTER id;

ALTER TABLE notification
    ADD COLUMN coffee_chat_id BIGINT NOT NULL AFTER target_id;

ALTER TABLE notification
    ADD CONSTRAINT fk_notification_target_id_from_member
        FOREIGN KEY (target_id)
            REFERENCES member (id);

ALTER TABLE notification
    ADD CONSTRAINT fk_notification_coffee_chat_id_from_coffee_chat
        FOREIGN KEY (coffee_chat_id)
            REFERENCES coffee_chat (id);
