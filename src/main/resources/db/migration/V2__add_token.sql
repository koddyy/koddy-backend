CREATE TABLE IF NOT EXISTS member_token
(
    id               BIGINT AUTO_INCREMENT PRIMARY KEY,
    member_id        BIGINT       NOT NULL UNIQUE,
    refresh_token    VARCHAR(250) NOT NULL UNIQUE,
    created_at       DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP,
    last_modified_at DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4;

ALTER TABLE member_token
    ADD CONSTRAINT fk_member_token_member_id_from_member
        FOREIGN KEY (member_id)
            REFERENCES member (id);
