package br.com.devquest.api.unittests.mocks;

import br.com.devquest.api.enums.Difficulty;
import br.com.devquest.api.enums.Technology;
import br.com.devquest.api.model.entities.Question;
import br.com.devquest.api.model.entities.QuestionOption;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@NoArgsConstructor
public class MockQuestion {

  public Question mockQuestion(Integer number) {
    var questionId = Integer.toUnsignedLong(number);
    List<QuestionOption> options = mockOptions();

    return Question.builder()
            .id(questionId)
            .technology(Technology.JAVA)
            .difficulty(Difficulty.BASICO)
            .text("Question Text " + number)
            .correctAnswer("A")
            .justification("Justification " + number)
            .createdAt(new Date())
            .options(options)
            .build();
  }

  private List<QuestionOption> mockOptions() {
    List<QuestionOption> options = new ArrayList<>();
    for (Integer i=1 ; i<=4 ; i++) {
      QuestionOption questionOption = QuestionOption.builder()
              .id(Integer.toUnsignedLong(i))
              .indicator("" +i+ "")
              .text("Text " + i)
              .build();

      options.add(questionOption);
    }

    return options;
  }

  public List<Question> mockQuestionList() {
    List<Question> questions = new ArrayList<>();
    for (Integer i=1 ; i<=14 ; i++) {
      questions.add(mockQuestion(i));
    }

    return questions;
  }

}
