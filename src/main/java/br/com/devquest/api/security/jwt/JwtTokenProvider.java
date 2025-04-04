package br.com.devquest.api.security.jwt;

import br.com.devquest.api.exceptions.InvalidJwtAuthenticationException;
import br.com.devquest.api.model.dtos.security.TokenDTO;
import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.interfaces.DecodedJWT;
import jakarta.annotation.PostConstruct;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.util.Base64;
import java.util.Date;
import java.util.List;

@Service
public class JwtTokenProvider {

  @Value("${security.jwt.token.secret-key}")
  private String secretKey;

  @Value("${security.jwt.token.expire-length}")
  private long validityInMilliseconds;

  @Autowired
  private UserDetailsService userDetailsService;

  Algorithm algorithm = null;

  @PostConstruct
  protected void init() {
    secretKey = Base64.getEncoder().encodeToString(secretKey.getBytes());
    algorithm = Algorithm.HMAC256(secretKey.getBytes());
  }

  public TokenDTO createAccessToken(String username, List<String> roles) {
    var now = new Date();
    var validity = new Date(now.getTime() + validityInMilliseconds);
    var accessToken = getAccessToken(username, roles, now, validity);
    var refreshToken = getRefreshToken(username, roles, now);

    return TokenDTO.builder()
            .username(username)
            .authenticated(true)
            .created(now)
            .expiration(validity)
            .accessToken(accessToken)
            .refreshToken(refreshToken)
            .build();
  }

  public TokenDTO refreshToken(String refreshToken) {
    var token = "";
    if (tokenContainsBearer(refreshToken)) token = refreshToken.substring("Bearer ".length());
    DecodedJWT decodedJWT = decodeToken(token);
    var username = decodedJWT.getSubject();
    List<String> roles = decodedJWT.getClaim("roles").asList(String.class);

    return createAccessToken(username, roles);
  }

  private boolean tokenContainsBearer(String token) {
    return token != null && token.startsWith("Bearer ");
  }

  private String getAccessToken(String username, List<String> roles, Date now, Date validity) {
    String issueURL = ServletUriComponentsBuilder.fromCurrentContextPath().build().toUriString();
    return JWT.create()
            .withClaim("roles", roles)
            .withIssuedAt(now)
            .withExpiresAt(validity)
            .withSubject(username)
            .withIssuer(issueURL)
            .sign(algorithm)
            .strip();
  }

  private String getRefreshToken(String username, List<String> roles, Date now) {
    Date refreshTokenValidity = new Date(now.getTime() + validityInMilliseconds * 3);
    return JWT.create()
            .withClaim("roles", roles)
            .withIssuedAt(now)
            .withExpiresAt(refreshTokenValidity)
            .withSubject(username)
            .sign(algorithm)
            .strip();
  }

  public Authentication getAuthentication(String token) {
    DecodedJWT decodedJWT = decodeToken(token);
    UserDetails userDetails = this.userDetailsService.loadUserByUsername(decodedJWT.getSubject());
    return new UsernamePasswordAuthenticationToken(userDetails, "", userDetails.getAuthorities());
  }

  private DecodedJWT decodeToken(String token) {
    JWTVerifier verifier = JWT.require(algorithm).build();
    DecodedJWT decodedJWT = verifier.verify(token);
    return decodedJWT;
  }

  public String resolveToken(HttpServletRequest request) {
    String token = request.getHeader("Authorization");
    if (tokenContainsBearer(token)) return token.substring("Bearer ".length());
    return null;
  }

  public boolean validateToken(String token) {
    DecodedJWT decodedJWT = decodeToken(token);
    try {
      if (decodedJWT.getExpiresAt().before(new Date())) return false;
      return true;
    } catch (Exception e) {
      throw new InvalidJwtAuthenticationException("Token JWT expirado ou inválido!");
    }
  }

}
