CREATE TABLE IF NOT EXISTS coffee_chat
(
    id               BIGINT AUTO_INCREMENT PRIMARY KEY,
    mentor_id        BIGINT      NOT NULL,
    mentee_id        BIGINT      NOT NULL,
    status           VARCHAR(50) NOT NULL,
    apply_reason     TEXT        NULL,
    suggest_reason   TEXT        NULL,
    question         TEXT        NULL,
    reject_reason    TEXT        NULL,
    start            DATETIME    NULL,
    end              DATETIME    NULL,
    chat_type        VARCHAR(30) NULL,
    chat_type_value  TEXT        NULL,
    created_at       DATETIME    NOT NULL DEFAULT CURRENT_TIMESTAMP,
    last_modified_at DATETIME    NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4;

ALTER TABLE coffee_chat
    ADD CONSTRAINT fk_coffee_chat_mentor_id_from_member
        FOREIGN KEY (mentor_id)
            REFERENCES member (id);

ALTER TABLE coffee_chat
    ADD CONSTRAINT fk_coffee_chat_mentee_id_from_member
        FOREIGN KEY (mentee_id)
            REFERENCES member (id);
