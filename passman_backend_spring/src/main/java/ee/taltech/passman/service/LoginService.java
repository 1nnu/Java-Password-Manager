package ee.taltech.passman.service;

import ee.taltech.passman.dto.login.LoginRequest;
import ee.taltech.passman.dto.master.InsertMasterPasswordRequest;
import ee.taltech.passman.entity.User;
import ee.taltech.passman.repository.UserRepository;
import jakarta.persistence.EntityNotFoundException;
import java.security.Principal;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

@Service
public class LoginService {

  private final PasswordService passwordService;
  private final UserRepository userRepository;
  private final ResponseService responseService;
  private final JwtService jwtService;

  public LoginService(
      PasswordService passwordService,
      UserRepository userRepository,
      ResponseService responseService,
      JwtService jwtService) {
    this.passwordService = passwordService;
    this.userRepository = userRepository;
    this.responseService = responseService;
    this.jwtService = jwtService;
  }

  public ResponseEntity<Object> handleLogin(LoginRequest request) {
    try {
      User user = getRequestUser(request.getUsername());
      if (passwordService.passwordMatches(request.getPassword(), user.getEncodedPassword())) {
        String jwt = jwtService.createJwtForUser(user);
        return responseService.generateLoginResponse(jwt);
      }
    } catch (EntityNotFoundException exception) {
      return responseService.generateBadRequestResponse(exception.getMessage());
    }
    return responseService.generateBadRequestResponse("Invalid login");
  }

  private User getRequestUser(String username) {
    User user = userRepository.findByUsername(username);
    if (user == null) {
      throw new EntityNotFoundException("Invalid login");
    }
    return user;
  }

  public ResponseEntity<Object> insertMasterPassword(
      InsertMasterPasswordRequest request, Principal principal) {
    try {
      User user = getRequestUser(principal.getName());
      if (passwordService.passwordMatches(request.getPassword(), user.getMasterPasswordHash())) {
        String jwt = jwtService.createVaultAccessJwtForUser(user);
        return responseService.generateLoginResponse(jwt);
      }
    } catch (EntityNotFoundException exception) {
      return responseService.generateBadRequestResponse(exception.getMessage());
    }
    return responseService.generateBadRequestResponse("Invalid credentials");
  }

  public ResponseEntity<Object> handleVaultLogout(String name) {
    try {
      User user = getRequestUser(name);
      String jwt = jwtService.createJwtForUser(user);
      return responseService.generateLoginResponse(jwt);
    } catch (EntityNotFoundException exception) {
      return responseService.generateBadRequestResponse(exception.getMessage());
    }
  }
}
