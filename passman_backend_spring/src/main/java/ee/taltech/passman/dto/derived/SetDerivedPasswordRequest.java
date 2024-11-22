package ee.taltech.passman.dto.derived;

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
public class SetDerivedPasswordRequest {
    @NotBlank(message = "Identifier cannot be empty.")
    @Pattern(regexp = "[0-9a-z_]+", flags = Pattern.Flag.CASE_INSENSITIVE, message = "Identifier contains invalid characters.")
    @Length(min = 3, max = 255, message = "Identifier must be at least 3 characters.")
    private String identifier;
}
