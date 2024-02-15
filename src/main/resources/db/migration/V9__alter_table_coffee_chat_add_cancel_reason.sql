ALTER TABLE coffee_chat
    ADD COLUMN cancel_reason TEXT AFTER suggest_reason;

ALTER TABLE coffee_chat
    MODIFY COLUMN reject_reason TEXT AFTER cancel_reason;
