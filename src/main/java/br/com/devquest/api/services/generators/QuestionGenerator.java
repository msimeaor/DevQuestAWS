package br.com.devquest.api.services.generators;

import br.com.devquest.api.enums.Difficulty;
import br.com.devquest.api.enums.Technology;
import br.com.devquest.api.model.entities.Question;
import br.com.devquest.api.model.entities.QuestionOption;
import br.com.devquest.api.repositories.QuestionRepository;
import br.com.devquest.api.utils.interfaces.IChatGPTClient;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;

import static br.com.devquest.api.utils.StringParser.*;

@Service
public class QuestionGenerator {

  private QuestionRepository repository;
  private IChatGPTClient chatGPTClient;

  public QuestionGenerator(QuestionRepository repository,
                           IChatGPTClient chatGPTClient) {

    this.repository = repository;
    this.chatGPTClient = chatGPTClient;
  }

  public Question createAndSave(Technology technology, Difficulty difficulty) {
    String questionString = chatGPTClient.generateQuestionString(technology, difficulty);
    Question question = Question.builder()
            .technology(technology)
            .difficulty(difficulty)
            .text(getContentBetweenFlags(questionString, "ENUNCIADO:", "ALTERNATIVAS:"))
            .correctAnswer(getContentBetweenFlags(questionString, "RESPOSTA CORRETA:", "JUSTIFICATIVA:"))
            .justification(getContentBetweenFlags(questionString, "JUSTIFICATIVA:", null))
            .createdAt(new Date())
            .build();

    createAndRelateOptionsWithQuestion(questionString, question);
    return repository.save(question);
  }

  private void createAndRelateOptionsWithQuestion(String questionString, Question question) {
    List<String> optionsString = getEnumeratorBetweenFlags(questionString, "ALTERNATIVAS:", "RESPOSTA CORRETA:");
    optionsString.forEach(optionString -> {
      String[] indicatorAndText = getArrayWithEnumeratorIndicatorAndText(optionString, "\\)");
      QuestionOption option = QuestionOption.builder()
              .indicator(indicatorAndText[0])
              .text(indicatorAndText[1])
              .build();
      question.addOption(option);
    });
  }

}
