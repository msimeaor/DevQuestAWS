package br.com.devquest.api.unittests.mocks;

import br.com.devquest.api.model.dtos.security.TokenDTO;
import lombok.NoArgsConstructor;

import java.util.Date;

@NoArgsConstructor
public class MockTokenDTO {

  public TokenDTO mockTokenDTO(Integer number) {
    Date now = new Date();
    return TokenDTO.builder()
            .username("Username" +number)
            .authenticated(true)
            .created(now)
            .expiration(new Date(now.getTime() + 3600000))
            .accessToken("Example of AccessToken")
            .refreshToken("Example of RefreshToken")
            .build();
  }

}
