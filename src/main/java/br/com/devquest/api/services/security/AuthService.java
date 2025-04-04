package br.com.devquest.api.services.security;

import br.com.devquest.api.exceptions.InvalidCredentialsException;
import br.com.devquest.api.model.dtos.security.AccountCredentialsDTO;
import br.com.devquest.api.model.dtos.security.TokenDTO;
import br.com.devquest.api.repositories.UserRepository;
import br.com.devquest.api.security.jwt.JwtTokenProvider;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class AuthService {

  private AuthenticationManager authenticationManager;
  private JwtTokenProvider tokenProvider;
  private UserRepository userRepository;

  public AuthService(AuthenticationManager authenticationManager,
                     JwtTokenProvider tokenProvider,
                     UserRepository userRepository) {
    this.authenticationManager = authenticationManager;
    this.tokenProvider = tokenProvider;
    this.userRepository = userRepository;
  }

  public ResponseEntity<TokenDTO> signin(AccountCredentialsDTO credentials) {
    try {
      authenticationManager.authenticate(
          new UsernamePasswordAuthenticationToken(
                  credentials.getUsername(),
                  credentials.getPassword()));
    } catch (AuthenticationException e) {
      throw new InvalidCredentialsException("Usuário ou senha incorretos!");
    }

    var user = userRepository.findByUsername(credentials.getUsername());
    var tokenResponse = tokenProvider.createAccessToken(credentials.getUsername(), user.getRoles());
    return ResponseEntity.ok(tokenResponse);
  }

  public ResponseEntity<TokenDTO> refreshToken(String username, String refreshToken) {
    var user = userRepository.findByUsername(username);
    if (user == null) throw new UsernameNotFoundException("Usuário " + username + " não encontrado");
    TokenDTO tokenDTO = tokenProvider.refreshToken(refreshToken);
    return ResponseEntity.ok(tokenDTO);
  }

}
