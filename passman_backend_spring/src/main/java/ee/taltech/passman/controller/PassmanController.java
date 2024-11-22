package ee.taltech.passman.controller;

import ee.taltech.passman.dto.derived.SetDerivedPasswordRequest;
import ee.taltech.passman.dto.login.LoginRequest;
import ee.taltech.passman.dto.master.InsertMasterPasswordRequest;
import ee.taltech.passman.dto.master.IsMasterSetRequest;
import ee.taltech.passman.dto.master.SetMasterPasswordRequest;
import ee.taltech.passman.dto.recovery.UserAccoutRecoveryRequest;
import ee.taltech.passman.dto.registration.RegistrationRequest;
import ee.taltech.passman.service.LoginService;
import ee.taltech.passman.service.PasswordService;
import ee.taltech.passman.service.RegistrationService;
import java.security.Principal;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api")
public class PassmanController {

  private final RegistrationService registrationService;
  private final LoginService loginService;
  private final PasswordService passwordService;

  public PassmanController(
      RegistrationService registrationService,
      LoginService loginService,
      PasswordService passwordService) {
    this.registrationService = registrationService;
    this.loginService = loginService;
    this.passwordService = passwordService;
  }

  @PostMapping(path = "/register")
  ResponseEntity<Object> handleRegistration(@RequestBody RegistrationRequest request) {
    return registrationService.handleRegistration(request);
  }

  @PostMapping(path = "/login")
  ResponseEntity<Object> handleLogin(@RequestBody LoginRequest request) {
    return loginService.handleLogin(request);
  }

  @GetMapping(path = "/ismasterset")
  ResponseEntity<Object> handleIsSetMasterPassword(Principal principal) {
    return passwordService.isMasterPasswordSet(principal.getName());
  }

  @PostMapping(path = "/setmaster")
  ResponseEntity<Object> handleSetMasterPassword(
      @RequestBody SetMasterPasswordRequest request, Principal principal) {
    return passwordService.setMasterPassword(request, principal.getName());
  }

  @PostMapping(path = "/insertmaster")
  ResponseEntity<Object> handleInsertMasterPassword(
      @RequestBody InsertMasterPasswordRequest request, Principal principal) {
    return loginService.insertMasterPassword(request, principal);
  }

  @PostMapping(path = "/logoutmaster")
  ResponseEntity<Object> handleLogoutFromVault(Principal principal) {
    return loginService.handleVaultLogout(principal.getName());
  }

  @PostMapping(path = "/keys")
  ResponseEntity<Object> handleSetDerivedPassword(
      @RequestBody SetDerivedPasswordRequest request, Principal principal) {
    return passwordService.createDerivedPassword(request, principal.getName());
  }

  @GetMapping(path = "/keys")
  ResponseEntity<Object> handleGetDerivedPasswordIdentifiers(Principal principal) {
    return passwordService.getAllDerivedPasswordIdentifiers(principal.getName());
  }

  @GetMapping(path = "/keys/{name}")
  ResponseEntity<Object> handleGetDerivedPassword(
      @PathVariable("name") String name, Principal principal) {
    return passwordService.getPasswordOfDerviedKeyByName(name, principal.getName());
  }

  @DeleteMapping(path = "/keys/{name}")
  ResponseEntity<Object> handleDeleteDerivedPassword(
      @PathVariable("name") String name, Principal principal) {
    return passwordService.deleteDerviedKeyByName(name, principal.getName());
  }

  @PostMapping(path = "/recovery")
  ResponseEntity<Object> handleAccountRecovery(@RequestBody UserAccoutRecoveryRequest request) {
    return passwordService.recoverAccount(request);
  }
}
