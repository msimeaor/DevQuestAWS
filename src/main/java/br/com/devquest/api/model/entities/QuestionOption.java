package br.com.devquest.api.model.entities;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "question_option")
public class QuestionOption implements Serializable {

  private static final long serialVersionUID = 1L;

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column
  private Long id;

  @Column(length = 1)
  private String indicator;

  @Column(columnDefinition = "TEXT")
  private String text;

  @ManyToOne
  @JoinColumn(name = "question_id", nullable = false)
  private Question question;

}
