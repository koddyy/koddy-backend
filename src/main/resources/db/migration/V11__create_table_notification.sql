CREATE TABLE IF NOT EXISTS notification
(
    id                BIGINT AUTO_INCREMENT PRIMARY KEY,
    source_member_id  BIGINT      NOT NULL,
    target_member_id  BIGINT      NOT NULL,
    notification_type VARCHAR(50) NOT NULL,
    message           TEXT        NOT NULL COMMENT 'JSON 데이터',
    is_read           TINYINT     NOT NULL,
    created_at        DATETIME    NOT NULL DEFAULT CURRENT_TIMESTAMP,
    last_modified_at  DATETIME    NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4;

ALTER TABLE notification
    ADD CONSTRAINT fk_mnotification_source_member_id_from_member
        FOREIGN KEY (source_member_id)
            REFERENCES member (id);

ALTER TABLE notification
    ADD CONSTRAINT fk_mnotification_target_member_id_from_member
        FOREIGN KEY (target_member_id)
            REFERENCES member (id);
