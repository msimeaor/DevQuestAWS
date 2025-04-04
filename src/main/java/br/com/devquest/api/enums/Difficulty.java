package br.com.devquest.api.enums;

import lombok.Getter;

@Getter
public enum Difficulty {

  BASICO("BASICO"), INTERMEDIARIO("INTERMEDIARIO"), AVANCADO("AVANCADO");

  private final String difficulty;

  Difficulty(String difficulty) {
    this.difficulty = difficulty;
  }

  public Difficulty getDifficultyByString(String difficultyString) {
    for (Difficulty d : Difficulty.values())
      if (d.getDifficulty().equalsIgnoreCase(difficultyString)) return d;

    throw new IllegalArgumentException("Dificuldade não encontrada!");
  }

}
