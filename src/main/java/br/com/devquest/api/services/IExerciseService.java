package br.com.devquest.api.services;

import br.com.devquest.api.enums.Difficulty;
import br.com.devquest.api.enums.Technology;
import br.com.devquest.api.model.dtos.ExerciseDTO;

public interface IExerciseService {

  ExerciseDTO generateExercise(String token, Technology technology, Difficulty difficulty);
  String answerExercise(String token, Long exerciseId);

}
