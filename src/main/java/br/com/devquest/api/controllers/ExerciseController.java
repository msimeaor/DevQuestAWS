package br.com.devquest.api.controllers;

import br.com.devquest.api.enums.Difficulty;
import br.com.devquest.api.enums.Technology;
import br.com.devquest.api.model.dtos.ExerciseDTO;
import br.com.devquest.api.services.IExerciseService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.websocket.server.PathParam;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/exercises")
@Tag(name = "ExerciseController", description = "Endpoints de manipulação de exercícios")
public class ExerciseController {

  private IExerciseService service;

  public ExerciseController(IExerciseService service) {
    this.service = service;
  }

  @Operation(summary = "Retornar um exercício não respondido pelo usuário")
  @GetMapping("/generate")
  public ResponseEntity<ExerciseDTO> generate(@RequestHeader("Authorization") String token,
                                              @RequestParam("technology") Technology technology,
                                              @RequestParam("difficulty") Difficulty difficulty) {

    ExerciseDTO exerciseDTO = service.generateExercise(token, technology, difficulty);
    return new ResponseEntity<>(exerciseDTO, HttpStatus.OK);
  }

  @Operation(summary = "Responder um exercício")
  @GetMapping("/answer/{id}")
  public ResponseEntity<String> answerExercise(@RequestHeader("Authorization") String token,
                                               @PathVariable("id") Long id) {

    String responseString = service.answerExercise(token, id);
    return new ResponseEntity<>(responseString, HttpStatus.OK);
  }

}
