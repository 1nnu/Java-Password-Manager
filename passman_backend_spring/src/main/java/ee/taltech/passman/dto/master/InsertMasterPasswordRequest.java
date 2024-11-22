package ee.taltech.passman.dto.master;

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
public class InsertMasterPasswordRequest {
    @NotBlank(message = "Can't provide empty password.")
    @Pattern(
            regexp = "[0-9a-z_!@#$%^()+\\-={}\\[\\]|\\\\:;<>.,?/~]+",
            flags = Pattern.Flag.CASE_INSENSITIVE,
            message = "Password must not contain invalid characters.")
    @Length(max = 255)
    private String password;
}
