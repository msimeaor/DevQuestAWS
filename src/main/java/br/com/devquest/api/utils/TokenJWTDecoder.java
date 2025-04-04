package br.com.devquest.api.utils;

import br.com.devquest.api.repositories.UserRepository;
import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.interfaces.DecodedJWT;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.Base64;

@Service
public class TokenJWTDecoder {

  @Value("${security.jwt.token.secret-key}")
  private String secretKey;

  @Value("${security.jwt.token.expire-length}")
  private long validityInMilliseconds;

  Algorithm algorithm = null;
  private UserRepository userRepository;

  public TokenJWTDecoder(UserRepository userRepository) {
    this.userRepository = userRepository;
  }

  @PostConstruct
  protected void init() {
    secretKey = Base64.getEncoder().encodeToString(secretKey.getBytes());
    algorithm = Algorithm.HMAC256(secretKey.getBytes());
  }

  public String getUsernameByToken(String token) {
    DecodedJWT decodedJWT = decodeToken(token);
    return decodedJWT.getSubject();
  }

  private DecodedJWT decodeToken(String token) {
    var tokenWithoutBearer = token.substring("Bearer ".length());
    JWTVerifier verifier = JWT.require(algorithm).build();
    DecodedJWT decodedJWT = verifier.verify(tokenWithoutBearer);
    return decodedJWT;
  }

}
