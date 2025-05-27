package ppks.projekt.backend.dto.questiondto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class QuestionDto {
    private Integer quizId;
    private String questionText;
    private List<String> options;
    private String correctAnswer;
}
