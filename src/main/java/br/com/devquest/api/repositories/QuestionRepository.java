package br.com.devquest.api.repositories;

import br.com.devquest.api.enums.Difficulty;
import br.com.devquest.api.enums.Technology;
import br.com.devquest.api.model.entities.Question;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.List;

@Repository
public interface QuestionRepository extends JpaRepository<Question, Long> {

  @Query("SELECT q FROM Question q WHERE q.technology = :technology AND q.difficulty = :difficulty")
  List<Question> findByTechnologyAndDifficulty(@PathVariable("technology") Technology technology,
                                               @PathVariable("difficulty") Difficulty difficulty);

  @Query(value = "SELECT COUNT(*) = 0 FROM User_Question WHERE user_id = :userId AND question_id = :questionId",
          nativeQuery = true)
  boolean questionWasNotAnsweredByUser(@PathVariable("questionId") Long questionId,
                                       @PathVariable("userId") Long userId);

}
