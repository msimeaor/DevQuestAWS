package br.com.devquest.api.model.entities;

import jakarta.persistence.*;
import lombok.*;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

@Getter
@Setter
@EqualsAndHashCode
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "users")
public class User implements UserDetails, Serializable {

  private static final long serialVersionUID = 1L;

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column
  private Long id;

  @Column(name = "full_name")
  private String fullname;

  @Column
  private String password;

  @Column(name = "user_name", unique = true)
  private String username;

  @Column(name = "account_non_expired")
  private Boolean accountNonExpired;

  @Column(name = "account_non_locked")
  private Boolean accountNonLocked;

  @Column(name = "credentials_non_expired")
  private Boolean credentialsNonExpired;

  @Column
  private Boolean enabled;

  @OneToOne(cascade = CascadeType.ALL)
  @JoinColumn(name = "user_profile_id", referencedColumnName = "id")
  private UserProfile userProfile;

  @OneToOne(cascade = CascadeType.ALL)
  @JoinColumn(name = "activity_statistics_id", referencedColumnName = "id")
  private ActivityStatistics activityStatistics;

  @ManyToMany(cascade = CascadeType.ALL)
  @JoinTable(name = "user_exercise",
    joinColumns = {@JoinColumn(name = "user_id")},
    inverseJoinColumns = {@JoinColumn(name = "exercise_id")}
  )
  private List<Exercise> exercises;

  @ManyToMany(cascade = CascadeType.ALL)
  @JoinTable(name = "user_question",
    joinColumns = {@JoinColumn(name = "user_id")},
    inverseJoinColumns = {@JoinColumn(name = "question_id")}
  )
  private List<Question> questions;

  @ManyToMany(fetch = FetchType.EAGER)
  @JoinTable(name = "user_permission",
    joinColumns = {@JoinColumn(name = "id_user")},
    inverseJoinColumns = {@JoinColumn(name = "id_permission")}
  )
  private List<Permission> permissions;

  public List<String> getRoles() {
    List<String> roles = new ArrayList<>();
    for (Permission permission : permissions) {
      roles.add(permission.getDescription());
    }
    return roles;
  }

  public void addExercise(Exercise exercise) {
    if (exercises == null) exercises = new ArrayList<>();
    exercises.add(exercise);
    exercise.addUser(this);
  }

  public void addQuestion(Question question) {
    if (questions == null) questions = new ArrayList<>();
    questions.add(question);
    question.addUser(this);
  }

  public void addPermission(Permission permission) {
    if (permissions == null) permissions = new ArrayList<>();
    permissions.add(permission);
  }

  @Override
  public Collection<? extends GrantedAuthority> getAuthorities() {
    return this.permissions;
  }

  @Override
  public boolean isAccountNonExpired() {
    return this.accountNonExpired;
  }

  @Override
  public boolean isAccountNonLocked() {
    return this.accountNonLocked;
  }

  @Override
  public boolean isCredentialsNonExpired() {
    return this.credentialsNonExpired;
  }

  @Override
  public boolean isEnabled() {
    return this.enabled;
  }

}
