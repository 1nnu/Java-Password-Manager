package ee.taltech.passman.dto.registration;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.validator.constraints.Length;

@JsonAutoDetect
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RegistrationRequest {

  @NotBlank(message = "Username cannot be empty.")
  @Pattern(
      regexp = "[0-9a-z_]+",
      flags = Pattern.Flag.CASE_INSENSITIVE,
      message = "Username contains invalid characters.")
  @Length(min = 3, max = 35, message = "Length of username must be between 3 and 35 characters.")
  private String username;

  @NotBlank(message = "Can't provide empty password.")
  @Pattern(
      regexp = "[0-9a-z_!@#$%^()+\\-={}\\[\\]|\\\\:;<>.,?/~]+",
      flags = Pattern.Flag.CASE_INSENSITIVE,
      message = "Password must not contain invalid characters.")
  @Length(min = 14, max = 255, message = "Length of password must be more than 14 characters.")
  private String password;
}
