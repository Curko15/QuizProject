package ppks.projekt.backend.dto.groupdto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class GroupDto {
        private String groupName;
        private String joinCode;
        private String creator;
    }

