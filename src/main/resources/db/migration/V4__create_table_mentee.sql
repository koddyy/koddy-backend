CREATE TABLE IF NOT EXISTS mentee
(
    id              BIGINT PRIMARY KEY,
    interest_school VARCHAR(100) NOT NULL,
    interest_major  VARCHAR(100) NOT NULL
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4;

ALTER TABLE mentee
    ADD CONSTRAINT fk_mentee_id_from_member
        FOREIGN KEY (id)
            REFERENCES member (id);
