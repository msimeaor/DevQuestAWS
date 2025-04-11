CREATE TABLE user_question (
  user_id BIGINT NOT NULL,
  question_id BIGINT NOT NULL,
  CONSTRAINT user_question_question_fk FOREIGN KEY (question_id)
    REFERENCES question (id) ON UPDATE CASCADE ON DELETE RESTRICT,
  CONSTRAINT user_question_user_fk FOREIGN KEY (user_id)
    REFERENCES users (id) ON UPDATE CASCADE ON DELETE RESTRICT
);
