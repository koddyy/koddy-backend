CREATE TABLE IF NOT EXISTS mentor
(
    id         BIGINT PRIMARY KEY,
    school     VARCHAR(100) NOT NULL,
    major      VARCHAR(100) NOT NULL,
    entered_in INT          NOT NULL
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4;

ALTER TABLE mentor
    ADD CONSTRAINT fk_mentor_id_from_member
        FOREIGN KEY (id)
            REFERENCES member (id);
