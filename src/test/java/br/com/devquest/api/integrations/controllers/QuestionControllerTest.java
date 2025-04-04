package br.com.devquest.api.integrations.controllers;

import br.com.devquest.api.configs.TestConfigs;
import br.com.devquest.api.dtos.AccountCredentialsDTOTest;
import br.com.devquest.api.dtos.TokenDTOTest;
import br.com.devquest.api.enums.Difficulty;
import br.com.devquest.api.enums.Status;
import br.com.devquest.api.enums.Technology;
import br.com.devquest.api.exceptions.response.ExceptionResponse;
import br.com.devquest.api.integrations.AbstractIntegrationTest;
import br.com.devquest.api.model.dtos.QuestionDTO;
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

import static io.restassured.RestAssured.*;
import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class QuestionControllerTest extends AbstractIntegrationTest {

  private static RequestSpecification specification;
  private static ObjectMapper mapper;
  private static AccountCredentialsDTOTest accountCredentialsDTO;
  private static String userAccessToken = "Bearer ";

  @BeforeEach
  void setUp() {
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
  void mustReturnAQuestionDTO_WhenGenerateWithValidParams() throws JsonProcessingException {
    var content = given(specification)
            .basePath(TestConfigs.QUESTION_CONTROLLER_BASEPATH + "/generate")
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

    var questionDTO = mapper.readValue(content, QuestionDTO.class);

    assertEquals(Technology.JAVA, questionDTO.getTechnology());
    assertEquals(Difficulty.BASICO, questionDTO.getDifficulty());
    assertEquals(4, questionDTO.getOptions().size());
    assertNotEquals("", questionDTO.getOptions().get(0).getIndicator());
    assertNotEquals("", questionDTO.getOptions().get(0).getText());
  }

  @Test
  @Order(2)
  void mustThrownAnException_WhenAnswerQuestionWithANonExistentQuestionId() throws JsonProcessingException {
    var nonExistentQuestionId = 800L;
    var content = given(specification)
            .basePath(TestConfigs.QUESTION_CONTROLLER_BASEPATH + "/answer")
            .header(TestConfigs.HEADER_PARAM_AUTHORIZATION, userAccessToken)
            .param("status", Status.CORRETO)
            .pathParam("id", nonExistentQuestionId) // This questionId does not exist in database
            .when()
              .get("/{id}")
            .then()
              .statusCode(404)
            .extract()
              .body()
                .asString();

    var exceptionResponse = mapper.readValue(content, ExceptionResponse.class);

    assertTrue(exceptionResponse.getMessage().equals("Questão para o ID " + nonExistentQuestionId + " não encontrada!"));
    assertTrue(exceptionResponse.getDetails().equals("uri=/api/questions/answer/800"));
  }

  @Test
  @Order(3)
  void mustReturnASuccessMessage_WhenAnswerQuestionWithValidParams() {
    var content = given(specification)
            .basePath(TestConfigs.QUESTION_CONTROLLER_BASEPATH + "/answer")
            .header(TestConfigs.HEADER_PARAM_AUTHORIZATION, userAccessToken)
            .param("status", Status.CORRETO)
            .pathParam("id", 1L) // This question was generated in first test
            .when()
              .get("/{id}")
            .then()
              .statusCode(200)
            .extract()
              .body()
                .asString();

    assertTrue(content.equals("Questão respondida com sucesso!"));
  }

  @Test
  @Order(4)
  void mustThrowAnException_WhenUserTriesToAnswerAnQuestionAlreadyAnsweredForHim() throws JsonProcessingException {
    var content = given(specification)
            .basePath(TestConfigs.QUESTION_CONTROLLER_BASEPATH + "/answer")
            .header(TestConfigs.HEADER_PARAM_AUTHORIZATION, userAccessToken)
            .param("status", Status.CORRETO)
            .pathParam("id", 1L)
            .when()
              .get("/{id}")
            .then()
              .statusCode(409)
            .extract()
              .body()
                .asString();

    var exceptionResponse = mapper.readValue(content, ExceptionResponse.class);

    assertTrue(exceptionResponse.getMessage().equals("Este usuário já respondeu essa questão!"));
    assertTrue(exceptionResponse.getDetails().equals("uri=/api/questions/answer/1"));
  }

  private static JsonNode extractObjectOfJSON(String content, String nodeObject) throws JsonProcessingException {
    JsonNode rootNode = mapper.readTree(content);
    return rootNode.get(nodeObject);
  }

  private static void startEntities() {
    accountCredentialsDTO = AccountCredentialsDTOTest.builder()
            .username("newmsimeaor")
            .password("123")
            .build();
  }

}