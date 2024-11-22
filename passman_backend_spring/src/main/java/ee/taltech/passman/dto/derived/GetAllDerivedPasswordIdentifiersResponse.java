package ee.taltech.passman.dto.derived;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@JsonAutoDetect
@Data
@NoArgsConstructor
@AllArgsConstructor
public class GetAllDerivedPasswordIdentifiersResponse {
    private List<String> identifiers;
}
