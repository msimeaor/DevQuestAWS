package br.com.devquest.api.unittests.services;

import br.com.devquest.api.model.entities.User;
import br.com.devquest.api.repositories.UserRepository;
import br.com.devquest.api.services.implementations.UserServiceImpl;
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
import org.springframework.http.HttpStatus;

import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@ExtendWith(MockitoExtension.class)
class UserServiceImplTest {

  private MockUser userInput;
  @InjectMocks
  private UserServiceImpl service;
  @Mock
  private UserRepository repository;
  @Mock
  private TokenJWTDecoder tokenJWTDecoder;

  @BeforeEach
  void setUp() {
    MockitoAnnotations.openMocks(this);
    userInput = new MockUser();
  }

  @Test
  void mustReturnAnUserInfoDTO_WhenGetUserInfoWithValidToken() {
    User user = userInput.mockUserWithActivityStatistics(1);
    when(tokenJWTDecoder.getUsernameByToken(anyString())).thenReturn(user.getUsername());
    when(repository.findByUsername(anyString())).thenReturn(user);
    var result = service.getUserInfo("Example-of-token");

    assertEquals(HttpStatus.OK, result.getStatusCode());
    assertEquals(user.getId(), result.getBody().getId());
    assertEquals(user.getFullname(), result.getBody().getFullname());
    assertEquals(user.getActivityStatistics().getCorrectQuestions(), result.getBody().getCorrectQuestions());
    assertEquals(user.getActivityStatistics().getExercisesCompleted(), result.getBody().getExercisesCompleted());
  }



}