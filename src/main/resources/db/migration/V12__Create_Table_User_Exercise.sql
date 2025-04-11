CREATE TABLE user_exercise (
  user_id BIGINT NOT NULL,
  exercise_id BIGINT NOT NULL,
  CONSTRAINT user_exercise_exercise_fk FOREIGN KEY (exercise_id)
    REFERENCES exercise (id) ON UPDATE NO ACTION ON DELETE NO ACTION,
  CONSTRAINT user_exercise_user_fk FOREIGN KEY (user_id)
    REFERENCES users (id) ON UPDATE NO ACTION ON DELETE NO ACTION
);
