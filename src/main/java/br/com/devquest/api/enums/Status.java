package br.com.devquest.api.enums;

import lombok.Getter;

@Getter
public enum Status {

  CORRETO("CORRETO"), INCORRETO("INCORRETO");

  private final String status;

  Status(String status) {
    this.status = status;
  }

  public Status getStatusByString(String statusString) {
    for (Status s : Status.values())
      if (s.getStatus().equalsIgnoreCase(statusString)) return s;

    throw new IllegalArgumentException("Status não encontrado!");
  }

}
