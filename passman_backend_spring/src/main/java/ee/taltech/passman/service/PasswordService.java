package ee.taltech.passman.service;

import ee.taltech.passman.dto.derived.SetDerivedPasswordRequest;
import ee.taltech.passman.dto.master.InsertMasterPasswordRequest;
import ee.taltech.passman.dto.master.SetMasterPasswordRequest;
import ee.taltech.passman.dto.recovery.UserAccoutRecoveryRequest;
import ee.taltech.passman.entity.DerivedUserKey;
import ee.taltech.passman.entity.User;
import ee.taltech.passman.exceptions.ValidationException;
import ee.taltech.passman.repository.DerivedKeyRepository;
import ee.taltech.passman.repository.UserRepository;
import jakarta.transaction.Transactional;
import java.security.NoSuchAlgorithmException;
import java.security.Principal;
import java.security.SecureRandom;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.KeySpec;
import java.util.List;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class PasswordService {

  public static final int ITERATIONS = 65536;
  public static final int KEY_LENGTH = 256;
  public static final int SALT_SIZE = 16;
  private final PasswordEncoder passwordEncoder;
  private final RequestValidatorService validatorService;
  private final ResponseService responseService;
  private final UserRepository userRepository;
  private final DerivedKeyRepository keyRepository;
  private final AccountRecoveryService accountRecoveryService;

  public PasswordService(
      PasswordEncoder passwordEncoder,
      RequestValidatorService validatorService,
      ResponseService responseService,
      UserRepository userRepository,
      DerivedKeyRepository keyRepository,
      AccountRecoveryService accountRecoveryService) {
    this.passwordEncoder = passwordEncoder;
    this.validatorService = validatorService;
    this.responseService = responseService;
    this.userRepository = userRepository;
    this.keyRepository = keyRepository;
    this.accountRecoveryService = accountRecoveryService;
  }

  public String hashPassword(String rawPassword) {
    return passwordEncoder.encode(rawPassword);
  }

  public boolean passwordMatches(String rawPassword, String hashedPassword) {
    return passwordEncoder.matches(rawPassword, hashedPassword);
  }

  public ResponseEntity<Object> setMasterPassword(SetMasterPasswordRequest request, String name) {
    try {
      validatorService.validateMasterPasswordRequest(request);
      User user = userRepository.findByUsername(name);
      String encodedMasterPassword = hashPassword(request.getPassword());
      user.setMasterPasswordHash(encodedMasterPassword);
      userRepository.save(user);
      return responseService.generateMasterPasswordRespone("Master password set");
    } catch (ValidationException exception) {
      return responseService.generateBadRequestResponse("Malformed password");
    }
  }

  public ResponseEntity<Object> createDerivedPassword(
      SetDerivedPasswordRequest request, String name) {
    try {
      validatorService.validateDerivedPasswordRequest(request);
      User user = userRepository.findByUsername(name);
      String salt = generateSalt();
      String derivedPassword = derivePasswordFromMasterPass(user.getMasterPasswordHash(), salt);
      DerivedUserKey derivedUserKey = new DerivedUserKey();
      derivedUserKey.setDerivedKey(derivedPassword);
      derivedUserKey.setDerivedKeyName(request.getIdentifier());
      derivedUserKey.setUser(user);
      keyRepository.save(derivedUserKey);
      return responseService.generateDerivedPasswordResponse();
    } catch (ValidationException exception) {
      return responseService.generateBadRequestResponse("Bad request");
    }
  }

  private String derivePasswordFromMasterPass(String masterPasswordHash, String salt) {
    try {

      KeySpec spec =
          new PBEKeySpec(masterPasswordHash.toCharArray(), salt.getBytes(), ITERATIONS, KEY_LENGTH);
      SecretKeyFactory factory = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA256");

      byte[] derivedKey = factory.generateSecret(spec).getEncoded();
      return bytesToHex(derivedKey);

    } catch (NoSuchAlgorithmException | InvalidKeySpecException e) {
      throw new RuntimeException("Error deriving password", e);
    }
  }

  private String generateSalt() {
    SecureRandom random = new SecureRandom();
    byte[] salt = new byte[SALT_SIZE];
    random.nextBytes(salt);
    return bytesToHex(salt);
  }

  private String bytesToHex(byte[] bytes) {
    StringBuilder sb = new StringBuilder();
    for (byte b : bytes) {
      sb.append(String.format("%02x", b));
    }
    return sb.toString();
  }

  public ResponseEntity<Object> getAllDerivedPasswordIdentifiers(String name) {
    User user = userRepository.findByUsername(name);
    List<DerivedUserKey> userKeys = keyRepository.findByUser(user);
    List<String> identifiers = userKeys.stream().map(DerivedUserKey::getDerivedKeyName).toList();
    return responseService.generateGetAllDerivedKeysResponse(identifiers);
  }

  public ResponseEntity<Object> getPasswordOfDerviedKeyByName(
      String keyName, String principalName) {
    User user = userRepository.findByUsername(principalName);
    DerivedUserKey key = keyRepository.findByDerivedKeyNameAndUser(keyName, user);
    if (key == null) {
      return responseService.generateBadRequestResponse("No key by given name found");
    }
    return responseService.generateGetDerivedKeyResponse(key.getDerivedKey());
  }

  @Transactional
  public ResponseEntity<Object> deleteDerviedKeyByName(String keyName, String principalName) {
    User user = userRepository.findByUsername(principalName);
    keyRepository.deleteByUserAndDerivedKeyName(user, keyName);
    return responseService.generateDeletedPasswordResponse();
  }

  public ResponseEntity<Object> recoverAccount(UserAccoutRecoveryRequest request) {
    try {
      validatorService.validateAccoutRecoveryRequest(request);
      User user = userRepository.findByUsername(request.getUsername());
      if (user == null) {
        throw new ValidationException("Could not find user");
      }
      if (accountRecoveryService.checkRecoveryKeyMatch(request.getRecoveryKey(), user)) {
        user.setEncodedPassword(hashPassword(request.getNewPassword()));
        String recoveryKey = accountRecoveryService.saveRecoveryKeyForUser(user);
        return responseService.generateAccountRecoveryResponse(recoveryKey);
      }
    } catch (ValidationException exception) {
      return responseService.generateBadRequestResponse("Bad request");
    }
    return responseService.generateBadRequestResponse("Bad request");
  }

    public ResponseEntity<Object> isMasterPasswordSet(String name) {
      User user = userRepository.findByUsername(name);
      if (user.getMasterPasswordHash() == null){
        return  responseService.generateIsMasterPasswordSet(false);
      } else {
        return  responseService.generateIsMasterPasswordSet(true);
      }
    }
}
