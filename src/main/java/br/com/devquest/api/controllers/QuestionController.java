package br.com.devquest.api.controllers;

import br.com.devquest.api.enums.Difficulty;
import br.com.devquest.api.enums.Status;
import br.com.devquest.api.enums.Technology;
import br.com.devquest.api.model.dtos.QuestionDTO;
import br.com.devquest.api.services.IQuestionService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/questions")
@Tag(name = "QuestionController", description = "Endpoints de manipulação de questões")
public class QuestionController {

  public IQuestionService service;

  public QuestionController(IQuestionService service) {
    this.service = service;
  }

  @Operation(summary = "Retornar uma questão não respondido pelo usuário")
  @GetMapping("/generate")
  public ResponseEntity<QuestionDTO> generate(@RequestHeader("Authorization") String token,
                                              @RequestParam("technology") Technology technology,
                                              @RequestParam("difficulty") Difficulty difficulty) {

    QuestionDTO questionDTO = service.generateQuestion(token, technology, difficulty);
    return new ResponseEntity<>(questionDTO, HttpStatus.OK);
  }

  @Operation(summary = "Responder uma questão")
  @GetMapping("/answer/{id}")
  public ResponseEntity<String> answer(@RequestHeader("Authorization") String token,
                                       @RequestParam("status") Status status,
                                       @PathVariable("id") Long questionId) {

    String responseString = service.answerQuestion(token, questionId, status);
    return new ResponseEntity<>(responseString, HttpStatus.OK);
  }

}
