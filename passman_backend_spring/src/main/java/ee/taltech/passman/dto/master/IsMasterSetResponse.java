package ee.taltech.passman.dto.master;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import ee.taltech.passman.dto.BadRequestResponse;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@JsonAutoDetect
@Data
@NoArgsConstructor
@AllArgsConstructor
public class IsMasterSetResponse {
    Boolean isSet;
}
