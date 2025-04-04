package br.com.devquest.api.unittests.services;

import br.com.devquest.api.enums.Difficulty;
import br.com.devquest.api.enums.Status;
import br.com.devquest.api.enums.Technology;
import br.com.devquest.api.exceptions.ActivityAlreadyAnsweredByUserException;
import br.com.devquest.api.exceptions.ResourceNotFoundException;
import br.com.devquest.api.model.entities.*;
import br.com.devquest.api.model.entities.Question;
import br.com.devquest.api.model.entities.Question;
import br.com.devquest.api.repositories.QuestionRepository;
import br.com.devquest.api.repositories.UserRepository;
import br.com.devquest.api.services.generators.QuestionGenerator;
import br.com.devquest.api.services.implementations.QuestionServiceImpl;
import br.com.devquest.api.unittests.mocks.MockQuestion;
import br.com.devquest.api.unittests.mocks.MockUser;
import br.com.devquest.api.utils.TokenJWTDecoder;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@ExtendWith(MockitoExtension.class)
class QuestionServiceImplTest {

  private MockQuestion questionInput;
  private MockUser userInput;
  @InjectMocks
  private QuestionServiceImpl service;
  @Mock
  private QuestionRepository repository;
  @Mock
  private UserRepository userRepository;
  @Mock
  private TokenJWTDecoder tokenJWTDecoder;
  @Mock
  private QuestionGenerator questionGenerator;

  @BeforeEach
  void setUp() {
    MockitoAnnotations.openMocks(this);
    questionInput = new MockQuestion();
    userInput = new MockUser();
  }

  @Test
  void mustReturnsANewQuestionDTO_WhenThereAreNoQuestionsWithThisTechnologyAndDifficulty() {
    Question question = questionInput.mockQuestion(1);
    User user = userInput.mockUser(1);
    when(userRepository.findByUsername(anyString())).thenReturn(user);
    when(tokenJWTDecoder.getUsernameByToken(anyString())).thenReturn(user.getUsername());
    when(repository.findByTechnologyAndDifficulty(any(Technology.class), any(Difficulty.class)))
            .thenReturn(Collections.EMPTY_LIST);
    when(questionGenerator.createAndSave(any(Technology.class), any(Difficulty.class))).thenReturn(question);

    var result = service.generateQuestion("Example of token", Technology.JAVA, Difficulty.BASICO);

    assertEquals(question.getId(), result.getId());
    assertEquals(question.getTechnology(), result.getTechnology());
    assertEquals(question.getDifficulty(), result.getDifficulty());
    assertEquals(question.getText(), result.getText());
    assertEquals(question.getCorrectAnswer(), result.getCorrectAnswer());
    assertEquals(question.getJustification(), result.getJustification());
    assertEquals(4, result.getOptions().size());
    assertEquals(1, result.getOptions().get(0).getId());
    assertEquals("1", result.getOptions().get(0).getIndicator());
    assertEquals("Text " + 1, result.getOptions().get(0).getText());
  }

  @Test
  void mustReturnsAnQuestionAlreadyRegisteredInDatabase_ButNotAnsweredByUser() {
    List<Question> questions = questionInput.mockQuestionList();
    User user = userInput.mockUser(1);
    when(userRepository.findByUsername(anyString())).thenReturn(user);
    when(tokenJWTDecoder.getUsernameByToken(anyString())).thenReturn(user.getUsername());
    when(repository.findByTechnologyAndDifficulty(any(Technology.class), any(Difficulty.class)))
            .thenReturn(questions);
    when(repository.questionWasNotAnsweredByUser(anyLong(), anyLong())).thenReturn(true);

    Question firstQuestion = questions.get(0);
    var result = service.generateQuestion("Example of token", Technology.JAVA, Difficulty.BASICO);

    assertEquals(firstQuestion.getId(), result.getId());
    assertEquals(firstQuestion.getTechnology(), result.getTechnology());
    assertEquals(firstQuestion.getDifficulty(), result.getDifficulty());
    assertEquals(firstQuestion.getText(), result.getText());
    assertEquals(firstQuestion.getOptions().get(0).getId(), result.getOptions().get(0).getId());
  }

  @Test
  void mustReturnsANewQuestionDTO_WhenAlreadyExistsQuestionsInDatabase_ButAllOfThemAnsweredByUser() {
    List<Question> questions = questionInput.mockQuestionList();
    Question question = questionInput.mockQuestion(15);
    User user = userInput.mockUser(1);
    when(userRepository.findByUsername(anyString())).thenReturn(user);
    when(tokenJWTDecoder.getUsernameByToken(anyString())).thenReturn(user.getUsername());
    when(repository.findByTechnologyAndDifficulty(any(Technology.class), any(Difficulty.class)))
            .thenReturn(questions);
    when(repository.questionWasNotAnsweredByUser(anyLong(), anyLong())).thenReturn(false);
    when(questionGenerator.createAndSave(any(Technology.class), any(Difficulty.class))).thenReturn(question);

    var result = service.generateQuestion("Example of token", Technology.JAVA, Difficulty.BASICO);

    assertEquals(question.getId(), result.getId());
    assertEquals(question.getTechnology(), result.getTechnology());
    assertEquals(question.getDifficulty(), result.getDifficulty());
    assertEquals(question.getText(), result.getText());
    assertEquals(question.getOptions().get(0).getId(), result.getOptions().get(0).getId());
  }

  @Test
  void mustReturnsASuccessString_WhenQuestionExists_UserHasNotAnsweredIt_AndStatusIsCorrect() {
    Question question = questionInput.mockQuestion(1);
    User user = userInput.mockUserWithActivityStatistics(1);
    Integer userCorrectQuestionsBeforeTest = user.getActivityStatistics().getCorrectQuestions();

    when(repository.findById(anyLong())).thenReturn(Optional.of(question));
    when(tokenJWTDecoder.getUsernameByToken(anyString())).thenReturn("Example of token");
    when(userRepository.findByUsername(anyString())).thenReturn(user);
    when(repository.questionWasNotAnsweredByUser(anyLong(), anyLong())).thenReturn(true);

    var result = service.answerQuestion("Example of token", question.getId(), Status.CORRETO);

    assertEquals("Questão respondida com sucesso!", result);
    assertTrue(user.getQuestions().contains(question));
    assertTrue(user.getActivityStatistics().getCorrectQuestions() > userCorrectQuestionsBeforeTest);
    verify(userRepository).save(user);
  }

  @Test
  void mustReturnsASuccessString_WhenQuestionExists_UserHasNotAnsweredIt_AndStatusIsIncorrect() {
    Question question = questionInput.mockQuestion(1);
    User user = userInput.mockUserWithActivityStatistics(1);
    Integer userCorrectQuestionsBeforeTest = user.getActivityStatistics().getCorrectQuestions();

    when(repository.findById(anyLong())).thenReturn(Optional.of(question));
    when(tokenJWTDecoder.getUsernameByToken(anyString())).thenReturn("Example of token");
    when(userRepository.findByUsername(anyString())).thenReturn(user);
    when(repository.questionWasNotAnsweredByUser(anyLong(), anyLong())).thenReturn(true);

    var result = service.answerQuestion("Example of token", question.getId(), Status.INCORRETO);

    assertEquals("Questão respondida com sucesso!", result);
    assertTrue(user.getQuestions().contains(question));
    assertTrue(user.getActivityStatistics().getCorrectQuestions() == userCorrectQuestionsBeforeTest);
    verify(userRepository).save(user);
  }

  @Test
  void mustThrowAnException_WhenQuestionNotExistsInDatabase() {
    Long invalidQuestionId = 800L;

    when(repository.findById(anyLong())).thenReturn(Optional.empty());

    Exception exception = assertThrows(ResourceNotFoundException.class, () -> {
      service.answerQuestion("example of token", invalidQuestionId, Status.CORRETO);
    });

    assertEquals(ResourceNotFoundException.class, exception.getClass());
    assertEquals("Questão para o ID " + invalidQuestionId + " não encontrada!", exception.getMessage());
  }

  @Test
  void mustThrowAnException_WhenQuestionExistsInDatabse_ButItsAlreadyAnsweredByUser() {
    Question question = questionInput.mockQuestion(1);
    User user = userInput.mockUserWithActivityStatistics(1);

    when(repository.findById(anyLong())).thenReturn(Optional.of(question));
    when(tokenJWTDecoder.getUsernameByToken(anyString())).thenReturn(user.getUsername());
    when(userRepository.findByUsername(anyString())).thenReturn(user);
    when(repository.questionWasNotAnsweredByUser(anyLong(), anyLong())).thenReturn(false);

    Exception exception = assertThrows(ActivityAlreadyAnsweredByUserException.class, () -> {
      service.answerQuestion("Example of token", question.getId(), Status.CORRETO);
    });

    assertEquals(ActivityAlreadyAnsweredByUserException.class, exception.getClass());
    assertEquals("Este usuário já respondeu essa questão!", exception.getMessage());
  }


}