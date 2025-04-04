package br.com.devquest.api.services.implementations;

import br.com.devquest.api.enums.Difficulty;
import br.com.devquest.api.enums.Status;
import br.com.devquest.api.enums.Technology;
import br.com.devquest.api.exceptions.ActivityAlreadyAnsweredByUserException;
import br.com.devquest.api.exceptions.ResourceNotFoundException;
import br.com.devquest.api.model.dtos.QuestionDTO;
import br.com.devquest.api.model.entities.Question;
import br.com.devquest.api.model.entities.User;
import br.com.devquest.api.repositories.QuestionRepository;
import br.com.devquest.api.repositories.UserRepository;
import br.com.devquest.api.services.IQuestionService;
import br.com.devquest.api.services.generators.QuestionGenerator;
import br.com.devquest.api.utils.TokenJWTDecoder;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.util.List;
import static br.com.devquest.api.mappers.DozerMapper.*;

@Service
public class QuestionServiceImpl implements IQuestionService {

  private QuestionRepository repository;
  private UserRepository userRepository;
  private TokenJWTDecoder tokenJWTDecoder;
  private QuestionGenerator questionGenerator;

  public QuestionServiceImpl(QuestionRepository repository,
                             UserRepository userRepository,
                             TokenJWTDecoder tokenJWTDecoder,
                             QuestionGenerator questionGenerator) {

    this.repository = repository;
    this.userRepository = userRepository;
    this.tokenJWTDecoder = tokenJWTDecoder;
    this.questionGenerator = questionGenerator;
  }

  @Transactional
  @Override
  public QuestionDTO generateQuestion(String token, Technology technology, Difficulty difficulty) {
    User user = userRepository.findByUsername(tokenJWTDecoder.getUsernameByToken(token));
    Question question;
    List<Question> questionsWithSameTechnologyAndDifficulty =
            repository.findByTechnologyAndDifficulty(technology, difficulty);

    if (!questionsWithSameTechnologyAndDifficulty.isEmpty()) {
      question = searchForQuestionNotAnsweredByUser(questionsWithSameTechnologyAndDifficulty, user);
      if (question != null) return parseObject(question, QuestionDTO.class);
    }

    question = questionGenerator.createAndSave(technology, difficulty);
    return parseObject(question, QuestionDTO.class);
  }

  @Transactional
  @Override
  public String answerQuestion(String token, Long questionId, Status status) {
    Question question = repository.findById(questionId).orElseThrow(
            () -> new ResourceNotFoundException("Questão para o ID " + questionId + " não encontrada!"));
    User user = userRepository.findByUsername(tokenJWTDecoder.getUsernameByToken(token));
    if (userAlreadyAnsweredQuestion(user.getId(), question.getId()))
      throw new ActivityAlreadyAnsweredByUserException("Este usuário já respondeu essa questão!");
    registerAnswer(user, question, status);

    return "Questão respondida com sucesso!";
  }

  private Question searchForQuestionNotAnsweredByUser(List<Question> questions, User user) {
    return questions.stream()
            .filter(q -> repository.questionWasNotAnsweredByUser(q.getId(), user.getId()) == 1L ? true : false)
            .findFirst()
            .orElse(null);
  }

  private boolean userAlreadyAnsweredQuestion(Long userId, Long questionId) {
    return repository.questionWasNotAnsweredByUser(questionId, userId) == 1L ? false : true;
  }

  private void registerAnswer(User user, Question question, Status status) {
    user.addQuestion(question);
    if (status == Status.CORRETO)
      user.getActivityStatistics().setCorrectQuestions(user.getActivityStatistics().getCorrectQuestions() + 1);

    userRepository.save(user);
  }

}
