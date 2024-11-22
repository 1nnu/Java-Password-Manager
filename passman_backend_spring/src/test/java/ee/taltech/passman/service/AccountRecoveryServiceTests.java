package ee.taltech.passman.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

import ee.taltech.passman.repository.RecoveryDataRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class AccountRecoveryServiceTests {

  @Mock private RecoveryDataRepository recoveryDataRepository;

  @InjectMocks private AccountRecoveryService accountRecoveryService;

  @BeforeEach
  void setUp() {
    MockitoAnnotations.openMocks(this);
  }

  @Test
  void givenRequestForGeneratingRecoveryKey_whenFunctionExecutes_thenReturnRandomRecoveryString() {
    String recoveryString = accountRecoveryService.createRecoveryKey();

    assertThat(recoveryString).isNotEmpty().hasSize(32);
  }
}
