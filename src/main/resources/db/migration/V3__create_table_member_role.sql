CREATE TABLE IF NOT EXISTS member_role
(
    id        BIGINT AUTO_INCREMENT PRIMARY KEY,
    member_id BIGINT      NOT NULL,
    role_type VARCHAR(30) NOT NULL
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4;

ALTER TABLE member_role
    ADD CONSTRAINT fk_member_role_member_id_from_member
        FOREIGN KEY (member_id)
            REFERENCES member (id);
