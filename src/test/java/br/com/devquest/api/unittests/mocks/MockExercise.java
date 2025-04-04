package br.com.devquest.api.unittests.mocks;

import br.com.devquest.api.enums.Difficulty;
import br.com.devquest.api.enums.Technology;
import br.com.devquest.api.model.entities.Exercise;
import br.com.devquest.api.model.entities.ExerciseInstruction;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@NoArgsConstructor
public class MockExercise {

  public Exercise mockExercise(Integer number) {
    Long exerciseId = Integer.toUnsignedLong(number);
    List<ExerciseInstruction> instructions = mockInstructions();
    return Exercise.builder()
            .id(exerciseId)
            .technology(Technology.JAVA)
            .difficulty(Difficulty.BASICO)
            .content("Example of content")
            .createdAt(new Date())
            .instructions(instructions)
            .build();
  }

  private List<ExerciseInstruction> mockInstructions() {
    List<ExerciseInstruction> instructions = new ArrayList<>();
    for (Integer i=0 ; i<=2 ; i++) {
      ExerciseInstruction exerciseInstruction = ExerciseInstruction.builder()
              .id(Integer.toUnsignedLong(i))
              .text("Example of text" + i)
              .indicator("" + i + "")
              .build();

      instructions.add(exerciseInstruction);
    }

    return instructions;
  }

  public List<Exercise> mockExerciseList() {
    List<Exercise> exercises = new ArrayList<>();
    for (Integer i=0 ; i<=14 ; i++) {
      Exercise exercise = mockExercise(i);
      exercises.add(exercise);
    }

    return exercises;
  }

}
