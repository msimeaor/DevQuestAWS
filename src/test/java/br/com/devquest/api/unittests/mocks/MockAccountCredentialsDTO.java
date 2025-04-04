package br.com.devquest.api.unittests.mocks;

import br.com.devquest.api.model.dtos.security.AccountCredentialsDTO;
import lombok.NoArgsConstructor;

@NoArgsConstructor
public class MockAccountCredentialsDTO {

  public AccountCredentialsDTO generateAccountCredentialsDTO(Integer number) {
    return AccountCredentialsDTO.builder()
            .username("username" +number)
            .password("password" +number)
            .build();
  }

}
