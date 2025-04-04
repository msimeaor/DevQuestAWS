package br.com.devquest.api.model.entities;

import br.com.devquest.api.enums.Difficulty;
import br.com.devquest.api.enums.Technology;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "question")
public class Question implements Serializable {

  private static final long serialVersionUID = 1L;

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column
  private Long id;

  @Enumerated(EnumType.STRING)
  @Column
  private Technology technology;

  @Enumerated(EnumType.STRING)
  @Column
  private Difficulty difficulty;

  @Column(columnDefinition = "TEXT")
  private String text;

  @Column(name = "correct_answer", length = 1)
  private String correctAnswer;

  @Column(columnDefinition = "TEXT")
  private String justification;

  @Column(name = "created_at")
  private Date createdAt;

  @OneToMany(mappedBy = "question", cascade = CascadeType.ALL, orphanRemoval = true)
  private List<QuestionOption> options;

  @ManyToMany(mappedBy = "questions", cascade = CascadeType.ALL)
  private List<User> users;

  public void addOption(QuestionOption option) {
    if (options == null) options = new ArrayList<>();
    options.add(option);
    option.setQuestion(this);
  }

  public void addUser(User user) {
    if (users == null) users = new ArrayList<>();
    users.add(user);
  }

}
