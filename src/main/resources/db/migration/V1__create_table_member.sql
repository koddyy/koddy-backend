CREATE TABLE IF NOT EXISTS member
(
    id                BIGINT AUTO_INCREMENT PRIMARY KEY,
    email             VARCHAR(200) NULL UNIQUE,
    name              VARCHAR(100) NOT NULL,
    profile_image_url VARCHAR(250) NOT NULL,
    nationality       VARCHAR(50)  NOT NULL,
    introduction      TEXT         NULL,
    profile_complete  VARCHAR(20)  NOT NULL,
    status            VARCHAR(20)  NOT NULL,
    role              VARCHAR(30)  NOT NULL,
    type              VARCHAR(31)  NOT NULL,
    created_at        DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP,
    last_modified_at  DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4;
