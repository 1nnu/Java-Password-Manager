package ee.taltech.passman.dto.login;

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
public class LoginResponse {

    String jwtToken;

}
