package br.com.devquest.api.services;

import br.com.devquest.api.model.dtos.UserInfoDTO;
import org.springframework.http.ResponseEntity;

public interface IUserService {

  ResponseEntity<UserInfoDTO> getUserInfo(String token);

}
