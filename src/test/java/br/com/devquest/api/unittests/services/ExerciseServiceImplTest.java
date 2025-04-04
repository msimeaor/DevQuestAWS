package br.com.devquest.api.unittests.services;

import br.com.devquest.api.enums.Difficulty;
import br.com.devquest.api.enums.Technology;
import br.com.devquest.api.exceptions.ActivityAlreadyAnsweredByUserException;
import br.com.devquest.api.exceptions.ResourceNotFoundException;
import br.com.devquest.api.model.entities.Exercise;
import br.com.devquest.api.model.entities.User;
import br.com.devquest.api.repositories.ExerciseRepository;
import br.com.devquest.api.repositories.UserRepository;
import br.com.devquest.api.services.generators.ExerciseGenerator;
import br.com.devquest.api.services.implementations.ExerciseServiceImpl;
import br.com.devquest.api.unittests.mocks.MockExercise;
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
import static org.mockito.Mockito.*;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@ExtendWith(MockitoExtension.class)
class ExerciseServiceImplTest {

  private MockExercise exerciseInput;
  private MockUser userInput;
  @InjectMocks
  private ExerciseServiceImpl service;
  @Mock
  private ExerciseRepository repository;
  @Mock
  private UserRepository userRepository;
  @Mock
  private TokenJWTDecoder tokenJWTDecoder;
  @Mock
  private ExerciseGenerator exerciseGenerator;

  @BeforeEach
  void setUp() {
    MockitoAnnotations.openMocks(this);
    exerciseInput = new MockExercise();
    userInput = new MockUser();
  }

  @Test
  void mustReturnsANewExerciseDTO_WhenThereAreNoExercisesWithThisTechnologyAndDifficulty() {
    Exercise exercise = exerciseInput.mockExercise(1);
    User user = userInput.mockUser(1);
    when(userRepository.findByUsername(anyString())).thenReturn(user);
    when(tokenJWTDecoder.getUsernameByToken(anyString())).thenReturn(user.getUsername());
    when(repository.findByTechnologyAndDifficulty(any(Technology.class), any(Difficulty.class)))
            .thenReturn(Collections.EMPTY_LIST);
    when(exerciseGenerator.createAndSave(any(Technology.class), any(Difficulty.class))).thenReturn(exercise);

    var result = service.generateExercise("Example of token", Technology.JAVA, Difficulty.BASICO);

    assertEquals(exercise.getId(), result.getId());
    assertEquals(exercise.getTechnology(), result.getTechnology());
    assertEquals(exercise.getDifficulty(), result.getDifficulty());
    assertEquals(exercise.getContent(), result.getContent());
    assertEquals(exercise.getInstructions().get(0).getId(), result.getInstructions().get(0).getId());
  }

  @Test
  void mustReturnsAnExerciseAlreadyRegisteredInDatabase_ButNotAnsweredByUser() {
    List<Exercise> exercises = exerciseInput.mockExerciseList();
    User user = userInput.mockUser(1);
    when(userRepository.findByUsername(anyString())).thenReturn(user);
    when(tokenJWTDecoder.getUsernameByToken(anyString())).thenReturn(user.getUsername());
    when(repository.findByTechnologyAndDifficulty(any(Technology.class), any(Difficulty.class)))
            .thenReturn(exercises);
    when(repository.exerciseWasNotAnsweredByUser(anyLong(), anyLong())).thenReturn(1L);

    Exercise firstExercise = exercises.get(0);
    var result = service.generateExercise("Example of token", Technology.JAVA, Difficulty.BASICO);

    assertEquals(firstExercise.getId(), result.getId());
    assertEquals(firstExercise.getTechnology(), result.getTechnology());
    assertEquals(firstExercise.getDifficulty(), result.getDifficulty());
    assertEquals(firstExercise.getContent(), result.getContent());
    assertEquals(firstExercise.getInstructions().get(0).getId(), result.getInstructions().get(0).getId());
  }

  @Test
  void mustReturnsANewExerciseDTO_WhenAlreadyExistsExercisesInDatabase_ButAllOfThemAnsweredByUser() {
    List<Exercise> exercises = exerciseInput.mockExerciseList();
    Exercise exercise = exerciseInput.mockExercise(15);
    User user = userInput.mockUser(1);
    when(userRepository.findByUsername(anyString())).thenReturn(user);
    when(tokenJWTDecoder.getUsernameByToken(anyString())).thenReturn(user.getUsername());
    when(repository.findByTechnologyAndDifficulty(any(Technology.class), any(Difficulty.class)))
            .thenReturn(exercises);
    when(repository.exerciseWasNotAnsweredByUser(anyLong(), anyLong())).thenReturn(0L);
    when(exerciseGenerator.createAndSave(any(Technology.class), any(Difficulty.class))).thenReturn(exercise);

    var result = service.generateExercise("Example of token", Technology.JAVA, Difficulty.BASICO);

    assertEquals(exercise.getId(), result.getId());
    assertEquals(exercise.getTechnology(), result.getTechnology());
    assertEquals(exercise.getDifficulty(), result.getDifficulty());
    assertEquals(exercise.getContent(), result.getContent());
    assertEquals(exercise.getInstructions().get(0).getId(), result.getInstructions().get(0).getId());
  }

  @Test
  void mustReturnsASuccessString_WhenExerciseExistsAndUserHasNotAnsweredIt() {
    Exercise exercise = exerciseInput.mockExercise(1);
    User user = userInput.mockUserWithActivityStatistics(1);
    var userExercisesCompletedBeforeTest = user.getActivityStatistics().getExercisesCompleted();

    when(repository.findById(anyLong())).thenReturn(Optional.of(exercise));
    when(tokenJWTDecoder.getUsernameByToken(anyString())).thenReturn(user.getUsername());
    when(userRepository.findByUsername(anyString())).thenReturn(user);
    when(repository.exerciseWasNotAnsweredByUser(anyLong(), anyLong())).thenReturn(1L);

    var result = service.answerExercise("Example of token", exercise.getId());

    assertEquals("Exercício resolvido com sucesso!", result);
    assertTrue(user.getExercises().contains(exercise));
    assertTrue(user.getActivityStatistics().getExercisesCompleted() > userExercisesCompletedBeforeTest);
    verify(userRepository).save(user);
  }

  @Test
  void mustThrowAnException_WhenExerciseNotExistsInDatabase() {
    Long invalidExerciseId = 800L;

    when(repository.findById(anyLong())).thenReturn(Optional.empty());

    Exception exception = assertThrows(ResourceNotFoundException.class, () -> {
      service.answerExercise("example of token", invalidExerciseId);
    });

    assertEquals(ResourceNotFoundException.class, exception.getClass());
    assertEquals("Exercício com id " + invalidExerciseId + " não encontrado!", exception.getMessage());
  }

  @Test
  void mustThrowAnException_WhenExerciseExistsInDatabse_ButItsAlreadyAnsweredByUser() {
    Exercise exercise = exerciseInput.mockExercise(1);
    User user = userInput.mockUserWithActivityStatistics(1);

    when(repository.findById(anyLong())).thenReturn(Optional.of(exercise));
    when(tokenJWTDecoder.getUsernameByToken(anyString())).thenReturn(user.getUsername());
    when(userRepository.findByUsername(anyString())).thenReturn(user);
    when(repository.exerciseWasNotAnsweredByUser(anyLong(), anyLong())).thenReturn(0L);

    Exception exception = assertThrows(ActivityAlreadyAnsweredByUserException.class, () -> {
      service.answerExercise("Example of token", exercise.getId());
    });

    assertEquals(ActivityAlreadyAnsweredByUserException.class, exception.getClass());
    assertEquals("Este usuário já concluiu este exercício anteriormente!", exception.getMessage());
  }

}