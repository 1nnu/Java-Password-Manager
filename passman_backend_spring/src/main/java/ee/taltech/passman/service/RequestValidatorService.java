package ee.taltech.passman.service;

import ee.taltech.passman.dto.derived.SetDerivedPasswordRequest;
import ee.taltech.passman.dto.master.SetMasterPasswordRequest;
import ee.taltech.passman.dto.recovery.UserAccoutRecoveryRequest;
import ee.taltech.passman.dto.registration.RegistrationRequest;
import ee.taltech.passman.exceptions.ValidationException;
import ee.taltech.passman.repository.UserRepository;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validator;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import org.springframework.stereotype.Service;

@Service
public class RequestValidatorService {

  private final Validator validator;
  private final UserRepository userRepository;

  public RequestValidatorService(Validator validator, UserRepository userRepository) {
    this.validator = validator;
    this.userRepository = userRepository;
  }

  public void validateRegistrationRequest(RegistrationRequest registrationRequest) {
    if (checkUniqueUsername(registrationRequest.getUsername())) {
      throw new ValidationException("Account with username already exists");
    }
    Set<ConstraintViolation<RegistrationRequest>> violations =
        validator.validate(registrationRequest);
    if (!violations.isEmpty()) {
      generateErrorMessageFromViolationsAndThrowException(violations);
    }
  }

  private boolean checkUniqueUsername(String username) {
    return null != userRepository.findByUsername(username);
  }

  public void validateMasterPasswordRequest(SetMasterPasswordRequest request) {
    Set<ConstraintViolation<SetMasterPasswordRequest>> violations = validator.validate(request);
    if (!violations.isEmpty()) {
      generateErrorMessageFromViolationsAndThrowException(violations);
    }
  }

  private <T> void generateErrorMessageFromViolationsAndThrowException(
      Set<ConstraintViolation<T>> violations) {
    List<String> violationsList = new ArrayList<>();
    violations.forEach(violation -> violationsList.add(violation.getMessage()));

    String errorMessage = String.join(", ", violationsList);
    throw new ValidationException(errorMessage);
  }

  public void validateDerivedPasswordRequest(SetDerivedPasswordRequest request) {
    Set<ConstraintViolation<SetDerivedPasswordRequest>> violations = validator.validate(request);
    if (!violations.isEmpty()) {
      generateErrorMessageFromViolationsAndThrowException(violations);
    }
  }

  public void validateAccoutRecoveryRequest(UserAccoutRecoveryRequest request) {
    Set<ConstraintViolation<UserAccoutRecoveryRequest>> violations = validator.validate(request);
    if (!violations.isEmpty()) {
      generateErrorMessageFromViolationsAndThrowException(violations);
    }
  }
}
