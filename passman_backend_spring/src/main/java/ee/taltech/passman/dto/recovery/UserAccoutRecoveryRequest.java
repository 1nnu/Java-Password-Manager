package ee.taltech.passman.dto.recovery;

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
public class UserAccoutRecoveryRequest {
    @NotBlank(message = "Can't provide empty recovery key.")
    @Pattern(
            regexp = "[0-9a-z]+",
            flags = Pattern.Flag.CASE_INSENSITIVE,
            message = "Recovery key must not contain invalid characters.")
    @Length(max = 50)
    private String recoveryKey;

    @NotBlank(message = "Username cannot be empty.")
    @Pattern(regexp = "[0-9a-z_]+", flags = Pattern.Flag.CASE_INSENSITIVE, message = "Username contains invalid characters.")
    private String username;

    @NotBlank(message = "Can't provide empty password.")
    @Pattern(
            regexp = "[0-9a-z_!@#$%^()+\\-={}\\[\\]|\\\\:;<>.,?/~]+",
            flags = Pattern.Flag.CASE_INSENSITIVE,
            message = "Password must not contain invalid characters.")
    @Length(max = 255)
    private String newPassword;
}
