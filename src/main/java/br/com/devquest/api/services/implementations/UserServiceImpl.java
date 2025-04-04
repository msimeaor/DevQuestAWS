package br.com.devquest.api.services.implementations;

import br.com.devquest.api.model.dtos.UserInfoDTO;
import br.com.devquest.api.model.entities.User;
import br.com.devquest.api.repositories.UserRepository;
import br.com.devquest.api.services.IUserService;
import br.com.devquest.api.utils.TokenJWTDecoder;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class UserServiceImpl implements UserDetailsService, IUserService {

  private UserRepository repository;
  private TokenJWTDecoder tokenJWTDecoder;

  public UserServiceImpl(UserRepository repository,
                         TokenJWTDecoder tokenJWTDecoder) {

    this.repository = repository;
    this.tokenJWTDecoder = tokenJWTDecoder;
  }

  @Override
  public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
    var user = repository.findByUsername(username);
    if (user != null) return user;
    else throw new UsernameNotFoundException("Usuário " +username+ " não encontrado!");
  }

  @Override
  public ResponseEntity<UserInfoDTO> getUserInfo(String token) {
    var username = tokenJWTDecoder.getUsernameByToken(token);
    User user = repository.findByUsername(username);
    UserInfoDTO userInfoDTO = UserInfoDTO.builder()
            .id(user.getId())
            .fullname(user.getFullname())
            .correctQuestions(user.getActivityStatistics().getCorrectQuestions())
            .exercisesCompleted(user.getActivityStatistics().getExercisesCompleted())
            .build();

    return new ResponseEntity<>(userInfoDTO, HttpStatus.OK);
  }

}
