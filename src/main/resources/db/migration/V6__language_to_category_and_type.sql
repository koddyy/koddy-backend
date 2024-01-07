ALTER TABLE available_language
    CHANGE COLUMN language language_category VARCHAR(20);

ALTER TABLE available_language
    ADD COLUMN language_type VARCHAR(20) NOT NULL;
