package ee.taltech.passman.service;

import ee.taltech.passman.dto.BadRequestResponse;
import ee.taltech.passman.dto.derived.GetAllDerivedPasswordIdentifiersResponse;
import ee.taltech.passman.dto.derived.GetSpecificDerivedPasswordResponse;
import ee.taltech.passman.dto.derived.SetDerivedPasswordResponse;
import ee.taltech.passman.dto.login.LoginResponse;
import ee.taltech.passman.dto.master.IsMasterSetResponse;
import ee.taltech.passman.dto.master.SetMasterPasswordResponse;
import ee.taltech.passman.dto.recovery.UserAccountRecoveryResponse;
import ee.taltech.passman.dto.registration.RegistrationResponse;
import java.util.List;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

@Service
public class ResponseService {

  public ResponseEntity<Object> generateBadRequestResponse(String errorMessage) {
    BadRequestResponse badRequestResponse = new BadRequestResponse(errorMessage);
    return new ResponseEntity<>(badRequestResponse, HttpStatusCode.valueOf(400));
  }

  public ResponseEntity<Object> generateRegistrationResponse(String recoveryKey) {
    RegistrationResponse registrationResponse = new RegistrationResponse(recoveryKey);
    return new ResponseEntity<>(registrationResponse, HttpStatusCode.valueOf(200));
  }

  public ResponseEntity<Object> generateLoginResponse(String jwt) {
    LoginResponse loginResponse = new LoginResponse();
    loginResponse.setJwtToken(jwt);
    return new ResponseEntity<>(loginResponse, HttpStatusCode.valueOf(200));
  }

  public ResponseEntity<Object> generateMasterPasswordRespone(String responseString) {
    SetMasterPasswordResponse passwordResponse = new SetMasterPasswordResponse();
    passwordResponse.setResponse(responseString);
    return new ResponseEntity<>(passwordResponse, HttpStatusCode.valueOf(201));
  }

  public ResponseEntity<Object> generateDerivedPasswordResponse() {
    SetDerivedPasswordResponse response = new SetDerivedPasswordResponse();
    response.setResponse("Generated password successfully");
    return new ResponseEntity<>(response, HttpStatusCode.valueOf(201));
  }

  public ResponseEntity<Object> generateGetAllDerivedKeysResponse(List<String> identifiers) {
    GetAllDerivedPasswordIdentifiersResponse response =
        new GetAllDerivedPasswordIdentifiersResponse();
    response.setIdentifiers(identifiers);
    return new ResponseEntity<>(response, HttpStatusCode.valueOf(200));
  }

  public ResponseEntity<Object> generateGetDerivedKeyResponse(String derivedKey) {
    GetSpecificDerivedPasswordResponse response = new GetSpecificDerivedPasswordResponse();
    response.setPassword(derivedKey);
    return new ResponseEntity<>(response, HttpStatusCode.valueOf(200));
  }

  public ResponseEntity<Object> generateDeletedPasswordResponse() {
    return new ResponseEntity<>(HttpStatusCode.valueOf(204));
  }

  public ResponseEntity<Object> generateAccountRecoveryResponse(String recoveryKey) {
    UserAccountRecoveryResponse response = new UserAccountRecoveryResponse();
    response.setRecoveryKey(recoveryKey);
    return new ResponseEntity<>(response, HttpStatusCode.valueOf(200));
  }

  public ResponseEntity<Object> generateIsMasterPasswordSet(boolean b) {
    IsMasterSetResponse response = new IsMasterSetResponse(b);
    return new ResponseEntity<>(response, HttpStatusCode.valueOf(200));
  }
}
