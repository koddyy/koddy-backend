ALTER TABLE mentor
    ADD COLUMN school_mail VARCHAR(200) NULL AFTER entered_in;

ALTER TABLE mentor
    ADD COLUMN proof_data_upload_url VARCHAR(200) NULL AFTER school_mail;

ALTER TABLE mentor
    ADD COLUMN auth_status VARCHAR(20) NULL AFTER proof_data_upload_url;
