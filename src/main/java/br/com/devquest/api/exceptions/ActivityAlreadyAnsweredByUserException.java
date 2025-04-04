package br.com.devquest.api.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.CONFLICT)
public class ActivityAlreadyAnsweredByUserException extends RuntimeException {
  public ActivityAlreadyAnsweredByUserException(String message) {
    super(message);
  }
}
