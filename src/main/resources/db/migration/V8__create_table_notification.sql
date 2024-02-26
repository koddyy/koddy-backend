CREATE TABLE IF NOT EXISTS notification
(
    id                          BIGINT AUTO_INCREMENT PRIMARY KEY,
    target_id                   BIGINT       NOT NULL,
    coffee_chat_id              BIGINT       NOT NULL,
    coffee_chat_status_snapshot VARCHAR(100) NOT NULL,
    notification_type           VARCHAR(50)  NOT NULL,
    is_read                     TINYINT      NOT NULL,
    created_at                  DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP,
    last_modified_at            DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4;

ALTER TABLE notification
    ADD CONSTRAINT fk_notification_target_id_from_member
        FOREIGN KEY (target_id)
            REFERENCES member (id);

ALTER TABLE notification
    ADD CONSTRAINT fk_notification_coffee_chat_id_from_coffee_chat
        FOREIGN KEY (coffee_chat_id)
            REFERENCES coffee_chat (id);
