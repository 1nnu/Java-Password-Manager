package ee.taltech.passman.service;

import ee.taltech.passman.dto.registration.RegistrationRequest;
import ee.taltech.passman.entity.User;
import ee.taltech.passman.exceptions.ValidationException;
import ee.taltech.passman.repository.UserRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

@Service
public class RegistrationService {

  private final RequestValidatorService validatorService;
  private final UserRepository userRepository;
  private final PasswordService passwordService;
  private final ResponseService responseService;
  private final AccountRecoveryService accountRecoveryService;

  public RegistrationService(
          RequestValidatorService validatorService,
          UserRepository userRepository,
          PasswordService passwordService,
          ResponseService responseService, AccountRecoveryService accountRecoveryService) {
    this.validatorService = validatorService;
    this.userRepository = userRepository;
    this.passwordService = passwordService;
    this.responseService = responseService;
      this.accountRecoveryService = accountRecoveryService;
  }

  public ResponseEntity<Object> handleRegistration(RegistrationRequest request) {
    try {
      validatorService.validateRegistrationRequest(request);
    } catch (ValidationException exception) {
      return responseService.generateBadRequestResponse(exception.getMessage());
    }
    String encodedPassword = passwordService.hashPassword(request.getPassword());
    User user = new User();
    user.setUsername(request.getUsername());
    user.setEncodedPassword(encodedPassword);
    userRepository.save(user);
    String recoveryKey = accountRecoveryService.saveRecoveryKeyForUser(user);
    return responseService.generateRegistrationResponse(recoveryKey);
  }
}
