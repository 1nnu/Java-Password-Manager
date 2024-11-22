package ee.taltech.passman.dto.registration;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import lombok.Data;
import lombok.NoArgsConstructor;

@JsonAutoDetect
@Data
@NoArgsConstructor
public class RegistrationResponse {

  public RegistrationResponse(String recoveryKey) {
    this.message = "Successfully registered";
    this.recoveryKey = recoveryKey;
  }

  private String message;
  private String recoveryKey;
}
