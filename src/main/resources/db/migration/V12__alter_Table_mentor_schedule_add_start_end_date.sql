ALTER TABLE mentor_schedule
    ADD COLUMN start_date DATE NOT NULL AFTER mentor_id;

ALTER TABLE mentor_schedule
    ADD COLUMN end_date DATE NOT NULL AFTER start_date;
