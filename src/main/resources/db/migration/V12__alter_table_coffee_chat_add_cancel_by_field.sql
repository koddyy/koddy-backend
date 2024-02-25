ALTER TABLE coffee_chat
    ADD COLUMN cancel_by BIGINT NULL AFTER status;

ALTER TABLE coffee_chat
    ADD CONSTRAINT fk_coffee_chat_cancel_by_from_member
        FOREIGN KEY (cancel_by)
            REFERENCES member (id);
