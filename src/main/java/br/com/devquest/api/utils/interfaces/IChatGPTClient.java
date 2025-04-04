package br.com.devquest.api.utils.interfaces;

import br.com.devquest.api.enums.Difficulty;
import br.com.devquest.api.enums.Technology;

public interface IChatGPTClient {

  String generateExerciseString(Technology technology, Difficulty difficulty);
  String generateQuestionString(Technology technology, Difficulty difficulty);

}
