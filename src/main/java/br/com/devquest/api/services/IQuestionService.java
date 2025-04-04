package br.com.devquest.api.services;

import br.com.devquest.api.enums.Difficulty;
import br.com.devquest.api.enums.Status;
import br.com.devquest.api.enums.Technology;
import br.com.devquest.api.model.dtos.QuestionDTO;

public interface IQuestionService {

  QuestionDTO generateQuestion(String token, Technology technology, Difficulty difficulty);
  String answerQuestion(String token, Long questionId, Status status);

}
