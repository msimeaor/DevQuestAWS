package br.com.devquest.api.model.dtos.security;

import lombok.*;

import java.io.Serializable;

@Getter
@Setter
@EqualsAndHashCode
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AccountCredentialsDTO implements Serializable {

  private static final long serialVersionUID = 1L;

  private String username;
  private String password;

}
