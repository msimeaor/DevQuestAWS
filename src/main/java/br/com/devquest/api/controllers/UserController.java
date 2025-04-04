package br.com.devquest.api.controllers;

import br.com.devquest.api.model.dtos.UserInfoDTO;
import br.com.devquest.api.services.IUserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/user")
@Tag(name = "UserController", description = "Endpoints de manipulação de usuários")
public class UserController {

  private IUserService service;

  public UserController(IUserService service) {
    this.service = service;
  }

  @Operation(summary = "Recuperar informações de usuários")
  @GetMapping("/getUserInfo")
  public ResponseEntity<UserInfoDTO> getUserInfo(@RequestHeader("Authorization") String token) {
    return service.getUserInfo(token);
  }

}
