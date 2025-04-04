package br.com.devquest.api.integrations.controllers;

import br.com.devquest.api.configs.TestConfigs;
import br.com.devquest.api.dtos.AccountCredentialsDTOTest;
import br.com.devquest.api.dtos.TokenDTOTest;
import br.com.devquest.api.exceptions.response.ExceptionResponse;
import br.com.devquest.api.integrations.AbstractIntegrationTest;
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
class AuthControllerTest extends AbstractIntegrationTest {

  private static RequestSpecification specification;
  private static ObjectMapper mapper;
  private static AccountCredentialsDTOTest accountCredentialsDTO;
  private static AccountCredentialsDTOTest invalidAccountCredentials;
  private static TokenDTOTest tokenDTOTest;

  @BeforeAll
  static void setUp() {
    mapper = new ObjectMapper();
    mapper.disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES);
    mapper.findAndRegisterModules();
    startEntities();
  }

  @Test
  @Order(1)
  void mustReturnATokenDTO_WhenSigninWithValidCredentials() throws JsonProcessingException {
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

    tokenDTOTest = mapper.treeToValue(extractObjectOfJSON(content, "body"), TokenDTOTest.class);

    assertEquals("msimeaor", tokenDTOTest.getUsername());
    assertEquals(true, tokenDTOTest.getAuthenticated());
    assertNotNull(tokenDTOTest.getAccessToken());
    assertNotNull(tokenDTOTest.getRefreshToken());
  }

  @Test
  @Order(2)
  void mustReturnAnErrorMessage_WhenSigninWithInvalidCredentials() throws JsonProcessingException {
    var content = given(specification)
            .basePath(TestConfigs.AUTH_CONTROLLER_BASEPATH + "/signin")
            .body(invalidAccountCredentials)
            .when()
              .post()
            .then()
              .statusCode(403)
            .extract()
              .body()
                .asString();

    var response = mapper.readValue(content, ExceptionResponse.class);

    assertEquals("Usuário ou senha incorretos!", response.getMessage());
  }

  @Test
  @Order(3)
  void mustReturnAnErrorMessage_WhenSigninWithNullableCredentials() throws JsonProcessingException {
    accountCredentialsDTO.setUsername("");
    var content = given(specification)
            .basePath(TestConfigs.AUTH_CONTROLLER_BASEPATH + "/signin")
            .body(accountCredentialsDTO)
            .when()
              .post()
            .then()
              .statusCode(403)
            .extract()
              .body()
                .asString();

    assertEquals("Usuário ou senha incorretos!", content);
  }

  @Test
  @Order(4)
  void mustReturnAnErrorMessage_WhenRefreshTokenWithNonExistentUsername() throws JsonProcessingException {
    var content = given(specification)
            .basePath(TestConfigs.AUTH_CONTROLLER_BASEPATH + "/refresh")
            .pathParams("username", "non-exists_username")
            .header("Authorization", tokenDTOTest.getRefreshToken())
            .when()
              .put("/{username}")
            .then()
              .statusCode(404)
            .extract()
              .body()
                .asString();

    var response = mapper.readValue(content, ExceptionResponse.class);

    assertEquals("Usuário non-exists_username não encontrado", response.getMessage());
    assertTrue(response.getDetails().contains("uri=/auth/refresh/non-exists_username"));
  }

  @Test
  @Order(5)
  void mustReturnAnErrorMessage_WhenRefreshTokenWithNullableUsername() {
    var content = given(specification)
            .basePath(TestConfigs.AUTH_CONTROLLER_BASEPATH + "/refresh")
            .pathParams("username", " ") // Empty username
            .header("Authorization", tokenDTOTest.getRefreshToken())
            .when()
              .put("/{username}")
            .then()
              .statusCode(403)
            .extract()
              .body()
                .asString();

    assertTrue(content.equals("Usuário não autenticado!"));
  }

  @Test
  @Order(6)
  void mustReturnATokenDTO_WhenRefreshTokenWithValidParams() throws JsonProcessingException, InterruptedException {
    Thread.sleep(1000); // Time for new token expiration validity to be updated
    var content = given(specification)
            .basePath(TestConfigs.AUTH_CONTROLLER_BASEPATH + "/refresh")
            .pathParams("username", tokenDTOTest.getUsername())
            .header("Authorization", "Bearer " + tokenDTOTest.getRefreshToken())
            .when()
              .put("/{username}")
            .then()
              .statusCode(200)
            .extract()
              .body()
                .asString();

    var refreshTokenDTO = mapper.treeToValue(extractObjectOfJSON(content, "body"), TokenDTOTest.class);

    assertEquals("msimeaor", refreshTokenDTO.getUsername());
    assertEquals(true, refreshTokenDTO.getAuthenticated());
    assertNotNull(refreshTokenDTO.getAccessToken());
    assertNotNull(refreshTokenDTO.getRefreshToken());
    assertNotEquals(tokenDTOTest.getAccessToken(), refreshTokenDTO.getAccessToken());
    assertNotEquals(tokenDTOTest.getRefreshToken(), refreshTokenDTO.getRefreshToken());
  }

  private static JsonNode extractObjectOfJSON(String content, String nodeObject) throws JsonProcessingException {
    JsonNode rootNode = mapper.readTree(content);
    return rootNode.get(nodeObject);
  }

  public static void startEntities() {
    accountCredentialsDTO = AccountCredentialsDTOTest.builder()
            .username("msimeaor")
            .password("123")
            .build();

    invalidAccountCredentials = AccountCredentialsDTOTest.builder()
            .username("matheus")
            .password("123")
            .build();

    tokenDTOTest = new TokenDTOTest();
  }

}