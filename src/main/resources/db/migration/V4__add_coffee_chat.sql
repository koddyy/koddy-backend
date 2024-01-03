CREATE TABLE IF NOT EXISTS coffee_chat
(
    id               BIGINT AUTO_INCREMENT PRIMARY KEY,
    applier_id       BIGINT      NOT NULL,
    target_id        BIGINT      NOT NULL,
    start_year       INT         NOT NULL,
    start_month      INT         NOT NULL,
    start_day        INT         NOT NULL,
    start_time       TIME        NOT NULL,
    end_year         INT         NOT NULL,
    end_month        INT         NOT NULL,
    end_day          INT         NOT NULL,
    end_time         TIME        NOT NULL,
    chat_type        VARCHAR(30) NOT NULL,
    chat_type_value  TEXT        NOT NULL,
    apply_reason     TEXT        NOT NULL,
    status           VARCHAR(30) NOT NULL,
    created_at       DATETIME    NOT NULL DEFAULT CURRENT_TIMESTAMP,
    last_modified_at DATETIME    NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4;

ALTER TABLE coffee_chat
    ADD CONSTRAINT fk_coffee_chat_applier_id_from_member
        FOREIGN KEY (applier_id)
            REFERENCES member (id);

ALTER TABLE coffee_chat
    ADD CONSTRAINT fk_coffee_chat_target_id_from_member
        FOREIGN KEY (target_id)
            REFERENCES member (id);
