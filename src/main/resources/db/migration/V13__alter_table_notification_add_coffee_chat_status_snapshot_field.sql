ALTER TABLE notification
    ADD COLUMN coffee_chat_status_snapshot VARCHAR(100) NOT NULL AFTER coffee_chat_id;
