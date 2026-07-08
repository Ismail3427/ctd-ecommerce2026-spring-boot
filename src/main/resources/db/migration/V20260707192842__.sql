ALTER TABLE retailers
    ADD CONSTRAINT uc_retailers_user UNIQUE (user_id);