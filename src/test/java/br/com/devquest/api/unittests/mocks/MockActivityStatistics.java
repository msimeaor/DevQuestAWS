package br.com.devquest.api.unittests.mocks;

import br.com.devquest.api.model.entities.ActivityStatistics;
import lombok.NoArgsConstructor;

@NoArgsConstructor
public class MockActivityStatistics {

  public ActivityStatistics mockActivityStatistics(Integer number) {
    Long activityStatisticsId = Integer.toUnsignedLong(number);
    return ActivityStatistics.builder()
            .id(activityStatisticsId)
            .correctQuestions(10)
            .exercisesCompleted(5)
            .build();
  }

}
