package br.com.devquest.api.model.dtos;

import br.com.devquest.api.enums.Difficulty;
import br.com.devquest.api.enums.Technology;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class QuestionDTO implements Serializable {

  private static final long serialVersionUID = 1L;

  private Long id;
  private Technology technology;
  private Difficulty difficulty;
  private String text;
  private String correctAnswer;
  private String justification;
  private List<QuestionOptionDTO> options;

}
