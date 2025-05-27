package ppks.projekt.backend.mappers;

import org.mapstruct.AfterMapping;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;
import ppks.projekt.backend.dto.quiz.QuizDto;
import ppks.projekt.backend.entitiy.Quiz;

@Mapper(componentModel = "spring")
public interface QuizMapper {
    QuizDto toQuizDto(Quiz quiz);
    Quiz toQuiz(QuizDto quizDto);

    @AfterMapping
    default void afterMapping(Quiz quiz, @MappingTarget QuizDto quizDto) {
        quizDto.setQuizId(quiz.getQuizId());
    }
}
