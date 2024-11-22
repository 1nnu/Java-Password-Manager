package ee.taltech.passman.password;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;

import ee.taltech.passman.repository.DerivedKeyRepository;
import ee.taltech.passman.repository.UserRepository;
import ee.taltech.passman.service.AccountRecoveryService;
import ee.taltech.passman.service.PasswordService;
import ee.taltech.passman.service.RequestValidatorService;
import ee.taltech.passman.service.ResponseService;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

class PasswordServiceTests {

  private static PasswordService passwordService;
  private RequestValidatorService requestValidatorService;
  private ResponseService responseService;
  private UserRepository userRepository;
  private DerivedKeyRepository derivedKeyRepository;
  private AccountRecoveryService accountRecoveryService;

  @BeforeAll
  static void init() {
    BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
    RequestValidatorService requestValidatorService = mock(RequestValidatorService.class);
    ResponseService responseService = mock(ResponseService.class);
    UserRepository userRepository = mock(UserRepository.class);
    DerivedKeyRepository keyRepository = mock(DerivedKeyRepository.class);
    AccountRecoveryService accountRecoveryService = mock(AccountRecoveryService.class);

    passwordService =
        new PasswordService(
            passwordEncoder,
            requestValidatorService,
            responseService,
            userRepository,
            keyRepository,
            accountRecoveryService);
  }

  @Test
  void givenUserRegistrationPassword_whenPasswordIsPassed_thenReturnHashedPassword() {
    String validPassword = "ThisIsAValidPassword@123";

    String hashedPassword = passwordService.hashPassword(validPassword);

    assertThat(hashedPassword).startsWith("$2a$10$").isASCII().hasSize(60);
  }

  @Test
  void
      givenUserRegistrationPassword_whenPasswordIsReturnedFromFunction_thenHashedPasswordCorrespondsToOriginalPassword() {
    String validPassword = "ThisIsAValidPassword@123";

    String hashedPassword = passwordService.hashPassword(validPassword);

    assertTrue(passwordService.passwordMatches(validPassword, hashedPassword));
  }
}
