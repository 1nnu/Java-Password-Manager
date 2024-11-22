package ee.taltech.passman.service;

import ee.taltech.passman.entity.RecoveryData;
import ee.taltech.passman.entity.User;
import ee.taltech.passman.repository.RecoveryDataRepository;
import java.util.Objects;
import org.apache.commons.lang3.RandomStringUtils;
import org.springframework.stereotype.Service;

@Service
public class AccountRecoveryService {

  private final RecoveryDataRepository recoveryDataRepository;

  private static final int RECOVERY_KEY_BOUND = 32;

  public AccountRecoveryService(RecoveryDataRepository recoveryDataRepository) {
    this.recoveryDataRepository = recoveryDataRepository;
  }

  public String createRecoveryKey() {
    return RandomStringUtils.randomAlphanumeric(RECOVERY_KEY_BOUND);
  }

  public String saveRecoveryKeyForUser(User user) {
    String recoveryKey = createRecoveryKey();
    RecoveryData userRecoveryData = new RecoveryData();
    userRecoveryData.setUser(user);
    userRecoveryData.setRecoveryKey(recoveryKey);
    recoveryDataRepository.save(userRecoveryData);
    return recoveryKey;
  }

  public boolean checkRecoveryKeyMatch(String recoveryKey, User user) {
    RecoveryData recoveryData = recoveryDataRepository.findByUser(user);
    return Objects.equals(recoveryData.getRecoveryKey(), recoveryKey);
  }
}
