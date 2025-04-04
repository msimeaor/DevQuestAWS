package br.com.devquest.api.dtos;

import lombok.*;

@Getter
@Setter
@EqualsAndHashCode
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AccountCredentialsDTOTest {

  private String username;
  private String password;

}
