ALTER TABLE mentor_schedule
    DROP COLUMN start_date;

ALTER TABLE mentor_schedule
    DROP COLUMN end_date;

ALTER TABLE mentor
    ADD COLUMN mentoring_start_date DATE NULL AFTER entered_in;

ALTER TABLE mentor
    ADD COLUMN mentoring_end_date DATE NULL AFTER mentoring_start_date;
