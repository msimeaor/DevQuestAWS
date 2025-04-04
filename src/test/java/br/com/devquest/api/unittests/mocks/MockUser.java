package br.com.devquest.api.unittests.mocks;

import br.com.devquest.api.model.entities.ActivityStatistics;
import br.com.devquest.api.model.entities.Permission;
import br.com.devquest.api.model.entities.User;
import lombok.NoArgsConstructor;

import java.util.List;

@NoArgsConstructor
public class MockUser {

  public User mockUser(Integer number) {
    Long userId = Integer.toUnsignedLong(number);
    Permission permission = Permission.builder()
            .id(1L)
            .description("ADMIN")
            .build();

    return User.builder()
            .id(userId)
            .fullname("Fullname" +number)
            .password("Password" +number)
            .username("Username" +number)
            .accountNonExpired(true)
            .accountNonLocked(true)
            .credentialsNonExpired(true)
            .permissions(List.of(permission))
            .enabled(true)
            .build();
  }

  public User mockUserWithActivityStatistics(Integer number) {
    Long userId = Integer.toUnsignedLong(number);
    ActivityStatistics activityStatistics = new MockActivityStatistics().mockActivityStatistics(number);
    Permission permission = Permission.builder()
            .id(1L)
            .description("ADMIN")
            .build();

    return User.builder()
            .id(userId)
            .fullname("Fullname" +number)
            .password("Password" +number)
            .username("Username" +number)
            .accountNonExpired(true)
            .accountNonLocked(true)
            .credentialsNonExpired(true)
            .enabled(true)
            .permissions(List.of(permission))
            .activityStatistics(activityStatistics)
            .build();
  }

}
