CREATE TABLE IF NOT EXISTS coffee_chat
(
    id               BIGINT AUTO_INCREMENT PRIMARY KEY,
    source_member_id BIGINT      NOT NULL,
    target_member_id BIGINT      NOT NULL,
    status           VARCHAR(30) NOT NULL,
    apply_reason     TEXT        NOT NULL,
    question         TEXT        NULL,
    reject_reason    TEXT        NULL,
    start_year       INT         NULL,
    start_month      INT         NULL,
    start_day        INT         NULL,
    start_time       TIME        NULL,
    end_year         INT         NULL,
    end_month        INT         NULL,
    end_day          INT         NULL,
    end_time         TIME        NULL,
    chat_type        VARCHAR(30) NULL,
    chat_type_value  TEXT        NULL,
    created_at       DATETIME    NOT NULL DEFAULT CURRENT_TIMESTAMP,
    last_modified_at DATETIME    NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4;

ALTER TABLE coffee_chat
    ADD CONSTRAINT fk_coffee_chat_source_member_id_from_member
        FOREIGN KEY (source_member_id)
            REFERENCES member (id);

ALTER TABLE coffee_chat
    ADD CONSTRAINT fk_coffee_chat_target_member_id_from_member
        FOREIGN KEY (target_member_id)
            REFERENCES member (id);
