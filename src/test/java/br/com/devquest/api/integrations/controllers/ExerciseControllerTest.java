package br.com.devquest.api.integrations.controllers;

import br.com.devquest.api.configs.TestConfigs;
import br.com.devquest.api.dtos.AccountCredentialsDTOTest;
import br.com.devquest.api.dtos.TokenDTOTest;
import br.com.devquest.api.enums.Difficulty;
import br.com.devquest.api.enums.Technology;
import br.com.devquest.api.exceptions.response.ExceptionResponse;
import br.com.devquest.api.integrations.AbstractIntegrationTest;
import br.com.devquest.api.model.dtos.ExerciseDTO;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.restassured.builder.RequestSpecBuilder;
import io.restassured.filter.log.LogDetail;
import io.restassured.filter.log.RequestLoggingFilter;
import io.restassured.filter.log.ResponseLoggingFilter;
import io.restassured.specification.RequestSpecification;
import org.junit.jupiter.api.*;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;

import static io.restassured.RestAssured.given;
import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class ExerciseControllerTest extends AbstractIntegrationTest {

  private static RequestSpecification specification;
  private static ObjectMapper mapper;
  private static AccountCredentialsDTOTest accountCredentialsDTO;
  private static String userAccessToken = "Bearer ";

  @BeforeAll
  static void setUp() {
    mapper = new ObjectMapper();
    mapper.disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES);
    mapper.findAndRegisterModules();
    startEntities();
  }

  @Test
  @Order(0)
  void authenticate() throws JsonProcessingException {
    specification = new RequestSpecBuilder()
            .addHeader(TestConfigs.HEADER_PARAM_ORIGIN, TestConfigs.ORIGIN_LOCAL)
            .setPort(TestConfigs.SERVER_PORT)
            .setContentType(MediaType.APPLICATION_JSON_VALUE)
            .addFilter(new RequestLoggingFilter(LogDetail.ALL))
            .addFilter(new ResponseLoggingFilter(LogDetail.ALL))
            .build();

    var content = given(specification)
            .basePath(TestConfigs.AUTH_CONTROLLER_BASEPATH + "/signin")
            .body(accountCredentialsDTO)
            .when()
            .post()
            .then()
            .statusCode(200)
            .extract()
            .body()
            .asString();

    var tokenDTOTest = mapper.treeToValue(extractObjectOfJSON(content, "body"), TokenDTOTest.class);
    userAccessToken = userAccessToken + tokenDTOTest.getAccessToken();
  }

  @Test
  @Order(1)
  void mustReturnAnExerciseDTO_WhenGenerateWithValidParams() throws JsonProcessingException {
    var content = given(specification)
            .basePath(TestConfigs.EXERCISE_CONTROLLER_BASEPATH + "/generate")
            .header(TestConfigs.HEADER_PARAM_AUTHORIZATION, userAccessToken)
            .param("technology", Technology.JAVA)
            .param("difficulty", Difficulty.BASICO)
            .when()
              .get()
            .then()
              .statusCode(200)
            .extract()
              .body()
                .asString();

    var exerciseDTO = mapper.readValue(content, ExerciseDTO.class);

    assertEquals(Technology.JAVA, exerciseDTO.getTechnology());
    assertEquals(Difficulty.BASICO, exerciseDTO.getDifficulty());
    assertEquals(3, exerciseDTO.getInstructions().size());
    assertNotEquals("", exerciseDTO.getInstructions().get(0).getIndicator());
    assertNotEquals("", exerciseDTO.getInstructions().get(0).getText());
  }

  @Test
  @Order(3)
  void mustThrownAnException_WhenAnswerExerciseWithANonExistentExerciseId() throws JsonProcessingException {
    var nonExistentExerciseId = 800L;
    var content = given(specification)
            .basePath(TestConfigs.EXERCISE_CONTROLLER_BASEPATH + "/answer")
            .header(TestConfigs.HEADER_PARAM_AUTHORIZATION, userAccessToken)
            .pathParam("id", nonExistentExerciseId) // This exerciseId does not exist in database
            .when()
              .get("/{id}")
            .then()
              .statusCode(404)
            .extract()
              .body()
                .asString();

    var exceptionResponse = mapper.readValue(content, ExceptionResponse.class);

    assertTrue(exceptionResponse.getMessage().equals("Exercício com id " + nonExistentExerciseId + " não encontrado!"));
    assertTrue(exceptionResponse.getDetails().equals("uri=/api/exercises/answer/800"));
  }

  @Test
  @Order(4)
  void mustReturnASuccessMessage_WhenAnswerExerciseWithValidParams() {
    var content = given(specification)
            .basePath(TestConfigs.EXERCISE_CONTROLLER_BASEPATH + "/answer")
            .header(TestConfigs.HEADER_PARAM_AUTHORIZATION, userAccessToken)
            .pathParam("id", 1L) // This exercise was generated in first test
            .when()
              .get("/{id}")
            .then()
              .statusCode(200)
            .extract()
              .body()
                .asString();

    assertTrue(content.equals("Exercício resolvido com sucesso!"));
  }

  @Test
  @Order(5)
  void mustThrowAnException_WhenUserTriesToAnswerAnExerciseAlreadyAnsweredForHim() throws JsonProcessingException {
    var content = given(specification)
            .basePath(TestConfigs.EXERCISE_CONTROLLER_BASEPATH + "/answer")
            .header(TestConfigs.HEADER_PARAM_AUTHORIZATION, userAccessToken)
            .pathParam("id", 1L)
            .when()
              .get("/{id}")
            .then()
              .statusCode(409)
            .extract()
              .body()
                .asString();

    var exceptionResponse = mapper.readValue(content, ExceptionResponse.class);

    assertTrue(exceptionResponse.getMessage().equals("Este usuário já concluiu este exercício anteriormente!"));
    assertTrue(exceptionResponse.getDetails().equals("uri=/api/exercises/answer/1"));
  }

  private static JsonNode extractObjectOfJSON(String content, String nodeObject) throws JsonProcessingException {
    JsonNode rootNode = mapper.readTree(content);
    return rootNode.get(nodeObject);
  }

  public static void startEntities() {
    accountCredentialsDTO = AccountCredentialsDTOTest.builder()
            .username("newmsimeaor")
            .password("123")
            .build();
  }

}