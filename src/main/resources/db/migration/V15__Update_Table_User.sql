ALTER TABLE users
    ADD COLUMN activity_statistics_id BIGINT,
    ADD COLUMN user_profile_id BIGINT;

ALTER TABLE users
    ADD CONSTRAINT uk_users_user_profile UNIQUE (user_profile_id),
    ADD CONSTRAINT uk_users_activity_statistics UNIQUE (activity_statistics_id),
    ADD CONSTRAINT fk_users_user_profile FOREIGN KEY (user_profile_id)
        REFERENCES user_profile (id)
        ON UPDATE CASCADE
        ON DELETE RESTRICT,
    ADD CONSTRAINT fk_users_activity_statistics FOREIGN KEY (activity_statistics_id)
        REFERENCES activity_statistics (id)
        ON UPDATE CASCADE
        ON DELETE RESTRICT;
