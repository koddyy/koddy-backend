CREATE TABLE IF NOT EXISTS member_language
(
    id                BIGINT AUTO_INCREMENT PRIMARY KEY,
    member_id         BIGINT      NOT NULL,
    language_category VARCHAR(20) NOT NULL,
    language_type     VARCHAR(20) NOT NULL
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4;

ALTER TABLE member_language
    ADD CONSTRAINT fk_member_language_member_id_from_member
        FOREIGN KEY (member_id)
            REFERENCES member (id);
