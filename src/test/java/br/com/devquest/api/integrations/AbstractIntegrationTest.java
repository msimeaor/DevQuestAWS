package br.com.devquest.api.integrations;

import org.flywaydb.core.Flyway;
import org.springframework.context.ApplicationContextInitializer;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.core.env.ConfigurableEnvironment;
import org.springframework.core.env.MapPropertySource;
import org.springframework.test.context.ContextConfiguration;
import org.testcontainers.containers.MySQLContainer;
import org.testcontainers.lifecycle.Startables;

import java.util.Map;
import java.util.stream.Stream;

@ContextConfiguration(initializers = AbstractIntegrationTest.Initializer.class)
public class AbstractIntegrationTest {

  static class Initializer implements ApplicationContextInitializer<ConfigurableApplicationContext> {
    static MySQLContainer<?> mysql = new MySQLContainer<>("mysql:8.0.34");

    private static void startContainers() {
      Startables.deepStart(Stream.of(mysql)).join();
      runFlywayMigrations();
    }

    private static void runFlywayMigrations() {
      Flyway flyway = Flyway.configure()
              .dataSource(mysql.getJdbcUrl(), mysql.getUsername(), mysql.getPassword())
              .load();
      flyway.migrate();
    }

    private static Map<String, String> createConnectionConfiguration() {
      return Map.of(
              "spring.datasource.url", mysql.getJdbcUrl(),
              "spring.datasource.username", mysql.getUsername(),
              "spring.datasource.password", mysql.getPassword()
      );
    }

    @Override
    public void initialize(ConfigurableApplicationContext applicationContext) {
      startContainers();
      ConfigurableEnvironment environment = applicationContext.getEnvironment();
      MapPropertySource testcontainers = new MapPropertySource(
              "testcontainers",
              (Map) createConnectionConfiguration());
      environment.getPropertySources().addFirst(testcontainers);
    }
  }

  protected static void resetDatabase() {
    Flyway flyway = Flyway.configure()
      .dataSource(
        Initializer.mysql.getJdbcUrl(),
        Initializer.mysql.getUsername(),
        Initializer.mysql.getPassword()
      )
      .cleanDisabled(false)
      .load();

    flyway.clean();
    flyway.migrate();
  }

}
