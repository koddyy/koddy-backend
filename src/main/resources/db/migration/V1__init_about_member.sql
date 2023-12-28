CREATE TABLE IF NOT EXISTS member
(
    id                BIGINT AUTO_INCREMENT PRIMARY KEY,
    email             VARCHAR(200) NOT NULL UNIQUE,
    email_status      VARCHAR(20)  NOT NULL,
    password          VARCHAR(200) NOT NULL,
    name              VARCHAR(100) NOT NULL,
    nationality       VARCHAR(50)  NOT NULL,
    profile_image_url VARCHAR(250) NOT NULL,
    type              VARCHAR(31)  NOT NULL,
    created_at        DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP,
    last_modified_at  DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4;

CREATE TABLE IF NOT EXISTS mentee
(
    id              BIGINT PRIMARY KEY,
    interest_major  VARCHAR(100) NOT NULL,
    interest_school VARCHAR(100) NOT NULL
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4;

CREATE TABLE IF NOT EXISTS mentor
(
    id           BIGINT PRIMARY KEY,
    school       VARCHAR(100) NOT NULL,
    major        VARCHAR(100) NOT NULL,
    grade        INT          NOT NULL,
    meeting_url  VARCHAR(250) NOT NULL,
    introduction TEXT         NOT NULL
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4;

CREATE TABLE IF NOT EXISTS available_language
(
    id        BIGINT AUTO_INCREMENT PRIMARY KEY,
    member_id BIGINT      NOT NULL,
    language  VARCHAR(30) NOT NULL,

    UNIQUE (member_id, language)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4;

CREATE TABLE IF NOT EXISTS member_role
(
    id        BIGINT AUTO_INCREMENT PRIMARY KEY,
    member_id BIGINT      NOT NULL,
    role_type VARCHAR(30) NOT NULL
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4;

CREATE TABLE IF NOT EXISTS mentor_chat_time
(
    id               BIGINT AUTO_INCREMENT PRIMARY KEY,
    mentor_id        BIGINT      NOT NULL,
    day              VARCHAR(20) NOT NULL,
    start_time       TIME        NOT NULL,
    end_time         TIME        NOT NULL,
    created_at       DATETIME    NOT NULL DEFAULT CURRENT_TIMESTAMP,
    last_modified_at DATETIME    NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4;

ALTER TABLE mentee
    ADD CONSTRAINT fk_mentee_id_from_member
        FOREIGN KEY (id)
            REFERENCES member (id);

ALTER TABLE mentor
    ADD CONSTRAINT fk_mentor_id_from_member
        FOREIGN KEY (id)
            REFERENCES member (id);

ALTER TABLE available_language
    ADD CONSTRAINT fk_available_language_member_id_from_member
        FOREIGN KEY (member_id)
            REFERENCES member (id);

ALTER TABLE member_role
    ADD CONSTRAINT fk_member_role_member_id_from_member
        FOREIGN KEY (member_id)
            REFERENCES member (id);

ALTER TABLE mentor_chat_time
    ADD CONSTRAINT fk_mentor_chat_time_mentor_id_from_mentor
        FOREIGN KEY (mentor_id)
            REFERENCES mentor (id);
