CREATE TABLE IF NOT EXISTS member
(
    id                BIGINT AUTO_INCREMENT PRIMARY KEY,
    social_provider   VARCHAR(30)  NOT NULL,
    social_id         VARCHAR(200) NULL UNIQUE,
    email             VARCHAR(200) NULL UNIQUE,
    name              VARCHAR(100) NOT NULL,
    nationality       VARCHAR(50)  NOT NULL,
    introduction      TEXT         NULL,
    profile_image_url VARCHAR(250) NULL,
    profile_complete  TINYINT      NOT NULL,
    status            VARCHAR(20)  NOT NULL,
    role              VARCHAR(30)  NOT NULL,
    type              VARCHAR(31)  NOT NULL,
    created_at        DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP,
    last_modified_at  DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4;
