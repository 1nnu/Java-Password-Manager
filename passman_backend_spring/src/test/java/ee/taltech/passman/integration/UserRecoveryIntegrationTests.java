package ee.taltech.passman.integration;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import ee.taltech.passman.dto.recovery.UserAccoutRecoveryRequest;
import ee.taltech.passman.dto.registration.RegistrationRequest;
import ee.taltech.passman.dto.registration.RegistrationResponse;
import ee.taltech.passman.entity.User;
import ee.taltech.passman.repository.DerivedKeyRepository;
import ee.taltech.passman.repository.RecoveryDataRepository;
import ee.taltech.passman.repository.UserJwtRepository;
import ee.taltech.passman.repository.UserRepository;
import ee.taltech.passman.security.jwt.JwtUtil;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

@ExtendWith(SpringExtension.class)
@SpringBootTest
@AutoConfigureMockMvc
class UserRecoveryIntegrationTests {

  private static final String CONTENT_TYPE = "application/json";
  @Autowired private MockMvc mockMvc;
  @Autowired private ObjectMapper objectMapper;
  @Autowired private UserRepository userRepository;
  @Autowired private UserJwtRepository userJwtRepository;
  @Autowired private DerivedKeyRepository derivedKeyRepository;
  @Autowired private RecoveryDataRepository recoveryDataRepository;
  @Autowired private JwtUtil jwtUtil;

  private User user;


  @Test
  void
      givenRegistrationRequestAndAccountRecoveryRequest_whenHandled_thenSetUserNewPasswordAndNewRecoveryKey()
          throws Exception {

    String username = "username";
    String password = "AValidAndStrongPassword";

    RegistrationRequest registrationRequest =
        RegistrationRequest.builder().username(username).password(password).build();

    String registrationRequestJson = objectMapper.writeValueAsString(registrationRequest);

    MvcResult registrationResult =
        mockMvc
            .perform(
                post("/api/register")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(registrationRequestJson))
            .andDo(print())
            .andExpect(status().isOk())
            .andReturn();
    assertEquals(CONTENT_TYPE, registrationResult.getResponse().getContentType());
    assertThat(registrationResult.getResponse().getContentAsString())
        .containsOnlyOnce("Successfully registered")
        .containsOnlyOnce("recoveryKey");

    RegistrationResponse registrationResponse =
        objectMapper.readValue(
            registrationResult.getResponse().getContentAsString(), RegistrationResponse.class);

    UserAccoutRecoveryRequest accoutRecoveryRequest = new UserAccoutRecoveryRequest();
    accoutRecoveryRequest.setUsername(username);
    accoutRecoveryRequest.setRecoveryKey(registrationResponse.getRecoveryKey());
    String newPassword = "NewValidAndStrongPassword";
    accoutRecoveryRequest.setNewPassword(newPassword);

    String recoveryRequestJson = objectMapper.writeValueAsString(accoutRecoveryRequest);

    MvcResult recoveryResult =
        mockMvc
            .perform(
                post("/api/recovery")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(recoveryRequestJson))
            .andDo(print())
            .andExpect(status().isOk())
            .andReturn();

    assertThat(recoveryResult.getResponse().getContentAsString()).containsOnlyOnce("\"recoveryKey\"").containsPattern("[a-zA-Z0-9]{32}");
  }

  @AfterEach
  public void cleanup() {
    recoveryDataRepository.deleteAll();
    userJwtRepository.deleteAll();
    derivedKeyRepository.deleteAll();
    userRepository.deleteAll();
  }
}
