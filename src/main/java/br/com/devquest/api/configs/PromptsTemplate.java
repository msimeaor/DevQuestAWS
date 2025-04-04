package br.com.devquest.api.configs;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class PromptsTemplate {

  @Bean
  public String exercisePrompt() {
    return "Você é um gerador de exercícios sobre %s para o nível %s. Sem inserir negrito, crie um exercício no formato abaixo:\n" +
            "\n" +
            "ENUNCIADO:\n" +
            "{Aqui deve ir o enunciado do exercício com no mínimo 2 linhas e no máximo 6 linhas}\n" +
            "\n" +
            "INSTRUÇÕES:\n" +
            "1. {Aqui deve vir a primeira instrução}\n" +
            "2. {Aqui deve vir a segunda instrução}\n" +
            "3. {Aqui deve vir a terceira instrução}";
  }

  @Bean
  public String questionPrompt() {
    return "Você é um gerador de questões sobre %s para o nível %s. Sem inserir negrito, crie uma questão no formato abaixo:\n" +
            "\n" +
            "ENUNCIADO:\n" +
            "{Aqui deve ir o enunciado da questão com no mínimo 2 linhas e no máximo 5 linhas}\n" +
            "\n" +
            "ALTERNATIVAS:\n" +
            "A) {Texto da alternativa A}\n" +
            "B) {Texto da alternativa B}\n" +
            "C) {Texto da alternativa C}\n" +
            "D) {Texto da alternativa D}\n" +
            "\n" +
            "RESPOSTA CORRETA:\n" +
            "{Letra da resposta correta (exemplo: A, B, C, D)}\n" +
            "\n" +
            "JUSTIFICATIVA:\n" +
            "Letra da resposta correta e a justificativa dela ser a correta}";
  }

}
