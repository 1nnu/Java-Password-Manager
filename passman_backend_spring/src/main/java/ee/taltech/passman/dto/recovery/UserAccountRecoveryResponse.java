package ee.taltech.passman.dto.recovery;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@JsonAutoDetect
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserAccountRecoveryResponse {
    private String recoveryKey;
}
