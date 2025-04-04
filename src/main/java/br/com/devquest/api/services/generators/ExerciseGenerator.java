package br.com.devquest.api.services.generators;

import br.com.devquest.api.enums.Difficulty;
import br.com.devquest.api.enums.Technology;
import br.com.devquest.api.model.entities.Exercise;
import br.com.devquest.api.model.entities.ExerciseInstruction;
import br.com.devquest.api.repositories.ExerciseRepository;
import br.com.devquest.api.utils.interfaces.IChatGPTClient;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;

import static br.com.devquest.api.utils.StringParser.*;

@Service
public class ExerciseGenerator {

  private ExerciseRepository repository;
  private IChatGPTClient chatGPTClient;

  public ExerciseGenerator(ExerciseRepository repository,
                           IChatGPTClient chatGPTClient) {

    this.repository = repository;
    this.chatGPTClient = chatGPTClient;
  }

  public Exercise createAndSave(Technology technology, Difficulty difficulty) {
    String exerciseString = chatGPTClient.generateExerciseString(technology, difficulty);
    Exercise exercise = Exercise.builder()
            .technology(technology)
            .difficulty(difficulty)
            .createdAt(new Date())
            .content(getContentBetweenFlags(exerciseString, "ENUNCIADO:", "INSTRUÇÕES:"))
            .build();

    createAndRelateInstructionsWithExercise(exerciseString, exercise);
    return repository.save(exercise);
  }

  private void createAndRelateInstructionsWithExercise(String exerciseString, Exercise exercise) {
    List<String> instructionsString = getEnumeratorBetweenFlags(exerciseString, "INSTRUÇÕES:", null);
    instructionsString.forEach(instructionString -> {
      String[] indicatorAndText = getArrayWithEnumeratorIndicatorAndText(instructionString, "\\.");
      ExerciseInstruction instruction = ExerciseInstruction.builder()
              .indicator(indicatorAndText[0])
              .text(indicatorAndText[1])
              .build();
      exercise.addInstruction(instruction);
    });
  }

}
