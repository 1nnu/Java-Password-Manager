package ee.taltech.passman.service;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;
import org.springframework.http.ResponseEntity;

class ResponseServiceTests {

  private final ResponseService responseService = new ResponseService();

  @Test
  void
      givenErrorMessage_whenResponseServiceExecuted_thenReturnsResponseEntityWithBadRequestResponseObject() {
    String errorMessage = "This is an error message.";

    ResponseEntity<Object> errorResponse = responseService.generateBadRequestResponse(errorMessage);

    assertThat(errorResponse.getStatusCode().value()).isEqualTo(400);
    assertThat(errorResponse.getBody()).hasFieldOrPropertyWithValue("error", errorMessage);
  }

  @Test
  void whenRegistrationResponseIsExecuted_thenReturnResponseEntityWithRegistrationResponseObject() {
    String successfulRegistrationResponse = "Successfully registered";
    String recoveryKey = "somekey";
    ResponseEntity<Object> registrationResponse = responseService.generateRegistrationResponse(recoveryKey);

    assertThat(registrationResponse.getStatusCode().value()).isEqualTo(200);
    assertThat(registrationResponse.getBody())
        .hasFieldOrPropertyWithValue("message", successfulRegistrationResponse);
  }
}
