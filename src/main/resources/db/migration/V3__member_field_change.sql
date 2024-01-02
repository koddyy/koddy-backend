ALTER TABLE member
    MODIFY introduction TEXT NULL;

ALTER TABLE mentor
    CHANGE grade entered_in INT NOT NULL;

ALTER TABLE mentor
    DROP COLUMN meeting_url;
