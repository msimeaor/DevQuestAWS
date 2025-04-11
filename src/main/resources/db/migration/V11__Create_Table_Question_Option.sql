CREATE TABLE question_option (
  id BIGINT PRIMARY KEY AUTO_INCREMENT,
  indicator VARCHAR(1),
  text TEXT,
  question_id BIGINT NOT NULL,
  CONSTRAINT question_option_question_fk FOREIGN KEY (question_id)
    REFERENCES question (id) ON UPDATE NO ACTION ON DELETE NO ACTION
);
