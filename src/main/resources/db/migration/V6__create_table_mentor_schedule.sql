CREATE TABLE IF NOT EXISTS mentor_schedule
(
    id          BIGINT AUTO_INCREMENT PRIMARY KEY,
    mentor_id   BIGINT      NOT NULL,
    start_date  DATE        NOT NULL,
    end_date    DATE        NOT NULL,
    day_of_week VARCHAR(20) NOT NULL,
    start_time  TIME        NOT NULL,
    end_time    TIME        NOT NULL
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4;

ALTER TABLE mentor_schedule
    ADD CONSTRAINT fk_mentor_schedule_mentor_id_from_mentor
        FOREIGN KEY (mentor_id)
            REFERENCES mentor (id);
