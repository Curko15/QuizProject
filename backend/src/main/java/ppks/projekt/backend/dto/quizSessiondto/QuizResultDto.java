package ppks.projekt.backend.dto.quizSessiondto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class QuizResultDto {
    private String quizTitle;
    private String accessCode;
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime startedAt;
    private List<ScoreEntry> leaderboard;

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class ScoreEntry {
        private String student;
        private int score;
    }
}
