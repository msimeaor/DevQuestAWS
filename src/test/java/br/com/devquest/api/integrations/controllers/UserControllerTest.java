package br.com.devquest.api.integrations.controllers;

import br.com.devquest.api.configs.TestConfigs;
import br.com.devquest.api.dtos.AccountCredentialsDTOTest;
import br.com.devquest.api.dtos.TokenDTOTest;
import br.com.devquest.api.integrations.AbstractIntegrationTest;
import br.com.devquest.api.model.dtos.UserInfoDTO;
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
class UserControllerTest extends AbstractIntegrationTest {

  private static RequestSpecification specification;
  private static ObjectMapper mapper;
  private static AccountCredentialsDTOTest accountCredentialsDTO;
  private static String userAccessToken;

  @BeforeAll
  static void setUp() {
    mapper = new ObjectMapper();
    mapper.disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES);
    mapper.findAndRegisterModules();
    startEntities();
    resetDatabase();
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
            .basePath("/auth/signin")
            .body(accountCredentialsDTO)
            .when()
              .post()
            .then()
              .statusCode(200)
            .extract()
              .body()
                .asString();

    var tokenDTOTest = mapper.treeToValue(extractObjectOfJSON(content, "body"), TokenDTOTest.class);
    userAccessToken = tokenDTOTest.getAccessToken();
  }

  @Test
  @Order(1)
  void mustReturnAnUserInfoDTO_WhenGetUserInfoWithAValidToken() throws JsonProcessingException {
    var content = given(specification)
            .basePath(TestConfigs.USERS_CONTROLLER_BASEPATH + "/getUserInfo")
            .header(TestConfigs.HEADER_PARAM_AUTHORIZATION, "Bearer " + userAccessToken)
            .when()
              .get()
            .then()
              .statusCode(200)
            .extract()
              .body()
                .asString();

    var userInfoDTO = mapper.readValue(content, UserInfoDTO.class);

    assertEquals(3L, userInfoDTO.getId());
    assertEquals("Matheus Simeão", userInfoDTO.getFullname());
    assertEquals(1, userInfoDTO.getCorrectQuestions());
    assertEquals(2, userInfoDTO.getExercisesCompleted());
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