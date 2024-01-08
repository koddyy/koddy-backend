CREATE TABLE IF NOT EXISTS mentor_schedule
(
    id               BIGINT AUTO_INCREMENT PRIMARY KEY,
    mentor_id        BIGINT      NOT NULL,
    day_of_week      VARCHAR(20) NOT NULL,
    start_time       TIME        NOT NULL,
    end_time         TIME        NOT NULL,
    created_at       DATETIME    NOT NULL DEFAULT CURRENT_TIMESTAMP,
    last_modified_at DATETIME    NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4;

ALTER TABLE mentor_schedule
    ADD CONSTRAINT fk_mentor_schedule_mentor_id_from_mentor
        FOREIGN KEY (mentor_id)
            REFERENCES mentor (id);
