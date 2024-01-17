CREATE TABLE IF NOT EXISTS mentor
(
    id                    BIGINT PRIMARY KEY,
    school                VARCHAR(100) NOT NULL,
    major                 VARCHAR(100) NOT NULL,
    entered_in            INT          NOT NULL,
    mentoring_start_date  DATE         NULL,
    mentoring_end_date    DATE         NULL,
    mentoring_time_unit   VARCHAR(20)  NULL,
    school_mail           VARCHAR(200) NULL,
    proof_data_upload_url VARCHAR(200) NULL,
    auth_status           VARCHAR(20)  NULL
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4;

ALTER TABLE mentor
    ADD CONSTRAINT fk_mentor_id_from_member
        FOREIGN KEY (id)
            REFERENCES member (id);
