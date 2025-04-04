package br.com.devquest.api.model.dtos;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class QuestionOptionDTO implements Serializable {

  private static final long serialVersionUID = 1L;

  private Long id;
  private String indicator;
  private String text;

}
