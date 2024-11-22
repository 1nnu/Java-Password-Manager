package ee.taltech.passman.dto.login;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@JsonAutoDetect
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class LoginRequest {
    @NotBlank(message = "Username cannot be empty.")
    @Pattern(regexp = "[0-9a-z_]+", flags = Pattern.Flag.CASE_INSENSITIVE, message = "Username contains invalid characters.")
    private String username;

    @NotBlank(message = "Can't provide empty password.")
    @Pattern(
            regexp = "[0-9a-z_!@#$%^()+\\-={}\\[\\]|\\\\:;<>.,?/~]+",
            flags = Pattern.Flag.CASE_INSENSITIVE,
            message = "Password must not contain invalid characters.")
    private String password;
}
