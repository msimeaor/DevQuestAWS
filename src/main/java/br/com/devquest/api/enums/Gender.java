package br.com.devquest.api.enums;

import lombok.Getter;

@Getter
public enum Gender {

  MASCULINO("MASCULINO"), FEMININO("FEMININO");

  private final String gender;

  Gender(String gender) {
    this.gender = gender;
  }

  public Gender getGenderByString(String genderString) {
    for (Gender g : Gender.values())
      if (g.getGender().equalsIgnoreCase(genderString)) return g;

    throw new IllegalArgumentException("Gênero não encontrado!");
  }

}
