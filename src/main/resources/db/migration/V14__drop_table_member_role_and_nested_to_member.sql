DROP TABLE member_role;

ALTER TABLE member
    ADD COLUMN role VARCHAR(30) NOT NULL AFTER status;
