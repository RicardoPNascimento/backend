ALTER TABLE events
    ADD CONSTRAINT user_id
        FOREIGN KEY (id_user) REFERENCES users (id_user);
