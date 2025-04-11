CREATE TABLE exercise_instruction (
  id BIGINT PRIMARY KEY AUTO_INCREMENT,
  indicator VARCHAR(1),
  text TEXT,
  exercise_id BIGINT NOT NULL,
  CONSTRAINT fk_exercise_instruction FOREIGN KEY (exercise_id)
    REFERENCES exercise (id)
    ON UPDATE NO ACTION
    ON DELETE NO ACTION
);
