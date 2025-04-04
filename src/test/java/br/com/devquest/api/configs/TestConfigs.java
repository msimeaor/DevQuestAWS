package br.com.devquest.api.configs;

public interface TestConfigs {

  int SERVER_PORT = 8888;
  String HEADER_PARAM_AUTHORIZATION = "Authorization";
  String HEADER_PARAM_ORIGIN = "Origin";

  String ORIGIN_LOCAL = "http://localhost:8080";
  String AUTH_CONTROLLER_BASEPATH = "/auth";
  String USERS_CONTROLLER_BASEPATH = "/api/user";
  String EXERCISE_CONTROLLER_BASEPATH = "/api/exercises";
  String QUESTION_CONTROLLER_BASEPATH = "/api/questions";

}
