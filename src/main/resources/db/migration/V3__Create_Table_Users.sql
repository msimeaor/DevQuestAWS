CREATE TABLE users (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    account_non_expired TINYINT(1),
    account_non_locked TINYINT(1),
    credentials_non_expired TINYINT(1),
    enabled TINYINT(1),
    full_name VARCHAR(255),
    password VARCHAR(255),
    user_name VARCHAR(255) UNIQUE
    -- question_statistics_id BIGINT UNIQUE,
    -- user_profile_id BIGINT UNIQUE,
    -- CONSTRAINT fk_question_statistics FOREIGN KEY (question_statistics_id) REFERENCES questions_statistics (id),
    -- CONSTRAINT fk_user_profile FOREIGN KEY (user_profile_id) REFERENCES user_profile (id)
);
