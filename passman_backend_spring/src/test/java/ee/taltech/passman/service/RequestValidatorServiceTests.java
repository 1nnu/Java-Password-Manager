package ee.taltech.passman.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;

import ee.taltech.passman.dto.registration.RegistrationRequest;
import ee.taltech.passman.exceptions.ValidationException;
import ee.taltech.passman.repository.DerivedKeyRepository;
import ee.taltech.passman.repository.RecoveryDataRepository;
import ee.taltech.passman.repository.UserJwtRepository;
import ee.taltech.passman.repository.UserRepository;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import org.junit.jupiter.api.*;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class RequestValidatorServiceTests {

  @Autowired private RequestValidatorService requestValidatorService;
  @Autowired private UserRepository userRepository;
  @Autowired private UserJwtRepository userJwtRepository;
  @Autowired private DerivedKeyRepository derivedKeyRepository;
  @Autowired private RecoveryDataRepository recoveryDataRepository;


  @Test
  void
      givenRegistrationRequest_whenRegistrationRequestUsernameIsNull_thenRequestValidatorServiceThrowsValidationExceptionWithMessage() {
    RegistrationRequest registrationRequest =
        RegistrationRequest.builder().username(null).password("somepasswordwhichislong").build();

    RuntimeException exception =
        assertThrows(
            ValidationException.class,
            () -> requestValidatorService.validateRegistrationRequest(registrationRequest));

    assertThat(exception).hasMessage("Username cannot be empty.");
  }

  @Test
  void
      givenRegistrationRequest_whenRegistrationRequestUsernameIsBlank_thenRequestValidatorServiceThrowsValidationExceptionWithMessage() {
    RegistrationRequest registrationRequest =
        RegistrationRequest.builder().username(" ").password("somepassword").build();

    RuntimeException exception =
        assertThrows(
            ValidationException.class,
            () -> requestValidatorService.validateRegistrationRequest(registrationRequest));

    assertThat(exception).hasMessageContaining("Username cannot be empty.");
  }

  @ParameterizedTest
  @ValueSource(strings = {"<>", "foo/zba\\", ":", ";hello", "<html>"})
  void
      givenRegistrationRequest_whenRegistrationRequestUsernameContainsInvalidCharacters_thenRequestValidatorServiceThrowsValidationExceptionWithMessage(
          String username) {
    RegistrationRequest registrationRequest =
        RegistrationRequest.builder().username(username).password("somepassword").build();

    RuntimeException exception =
        assertThrows(
            ValidationException.class,
            () -> requestValidatorService.validateRegistrationRequest(registrationRequest));

    assertThat(exception).hasMessageContaining("Username contains invalid characters.");
  }

  @Test
  void
      givenRegistrationRequest_whenRegistrationRequestPasswordIsBlank_thenRequestValidatorServiceThrowsValidationExceptionWithMessage() {
    RegistrationRequest registrationRequest =
        RegistrationRequest.builder().username("username").password("   \n").build();

    RuntimeException exception =
        assertThrows(
            ValidationException.class,
            () -> requestValidatorService.validateRegistrationRequest(registrationRequest));

    assertThat(exception).hasMessageContaining("Can't provide empty password.");
  }

  @ParameterizedTest
  @ValueSource(
      strings = {
        "password 123",
        "password*with^spaces",
        "tabs\tarebad",
        "\"quotes\"",
        "'single-quotes'",
        "`backticks`",
      })
  void
      givenRegistrationRequest_whenRegistrationRequestPasswordHasInvalidCharacters_thenRequestValidatorServiceThrowsValidationExceptionWithMessage(
          String password) {
    RegistrationRequest registrationRequest =
        RegistrationRequest.builder().username("username").password(password).build();

    RuntimeException exception =
        assertThrows(
            ValidationException.class,
            () -> requestValidatorService.validateRegistrationRequest(registrationRequest));

    assertThat(exception).hasMessageContaining("Password must not contain invalid characters.");
  }

  @Test
  void
      givenRegistrationRequest_whenRegistrationRequestUsernameIsAbove35Characters_thenRequestValidatorServiceThrowsValidationExceptionWithMessage() {
    RegistrationRequest registrationRequest =
        RegistrationRequest.builder()
            .username("AStringWhichIsMoreThanThirtyFiveCharactersInLength")
            .password("somepassword")
            .build();

    RuntimeException exception =
        assertThrows(
            ValidationException.class,
            () -> requestValidatorService.validateRegistrationRequest(registrationRequest));

    assertThat(exception)
        .hasMessageContaining("Length of username must be between 3 and 35 characters.");
  }

  @Test
  void
      givenRegistrationRequest_whenRegistrationRequestUsernameIsBelow35Characters_thenRequestValidatorServiceThrowsValidationServiceDoesNotThrowException() {
    RegistrationRequest registrationRequest =
        RegistrationRequest.builder()
            .username("ANormalName")
            .password("somepasswordwhichislong")
            .build();

    assertDoesNotThrow(
        () -> requestValidatorService.validateRegistrationRequest(registrationRequest));
  }

  @Test
  void
      givenRegistrationRequest_whenRegistrationRequestPasswordIsAbove14Characters_thenRequestValidatorServiceDoesNotThrowException() {
    RegistrationRequest registrationRequest =
        RegistrationRequest.builder()
            .username("ANormalName")
            .password("somepasswordwhichislong")
            .build();

    assertDoesNotThrow(
        () -> requestValidatorService.validateRegistrationRequest(registrationRequest));
  }

  @Test
  void
      givenRegistrationRequest_whenRegistrationRequestPasswordIsBelow14Characters_thenRequestValidatorServiceThrowsExceptionWithMessage() {
    RegistrationRequest registrationRequest =
        RegistrationRequest.builder().username("ANormalName").password("shortpassword").build();

    RuntimeException exception =
        assertThrows(
            ValidationException.class,
            () -> requestValidatorService.validateRegistrationRequest(registrationRequest));

    assertThat(exception)
        .hasMessageContaining("Length of password must be more than 14 characters.");
  }

  @AfterEach
  public void cleanup() {
    recoveryDataRepository.deleteAll();
    userJwtRepository.deleteAll();
    derivedKeyRepository.deleteAll();
    userRepository.deleteAll();
  }
}
