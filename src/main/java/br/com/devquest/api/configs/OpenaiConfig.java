package br.com.devquest.api.configs;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenaiConfig {

  @Bean
  public OpenAPI customerOpenAPI() {
    return new OpenAPI()
            .info(new Info()
              .title("DevQuest Application")
              .description("API para app gerador de questões sobre tecnologia")
              .version("1.0.0")
              .contact(new Contact()
                .name("Matheus Simeão dos Reis & Gabriel Stênio Pereira Tavares")
                .email("maatsimeao@gmail.com")
                .url("https://github.com/bagrielz/backend-devquest")
              )
            );
  }

}
