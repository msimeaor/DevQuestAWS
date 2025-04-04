package br.com.devquest.api.repositories;

import br.com.devquest.api.enums.Difficulty;
import br.com.devquest.api.enums.Technology;
import br.com.devquest.api.model.entities.Exercise;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.List;

@Repository
public interface ExerciseRepository extends JpaRepository<Exercise, Long> {

  @Query("SELECT e FROM Exercise e WHERE e.technology = :technology AND e.difficulty = :difficulty")
  List<Exercise> findByTechnologyAndDifficulty(@PathVariable("technology") Technology technology,
                                               @PathVariable("difficulty") Difficulty difficulty);

  @Query(value = "SELECT COUNT(*) = 0 FROM user_exercise WHERE exercise_id = :exerciseId AND user_id = :userId",
          nativeQuery = true)
  boolean exerciseWasNotAnsweredByUser(@PathVariable("exerciseId") Long exerciseId,
                                        @PathVariable("userId") Long userId);

}
