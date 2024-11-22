package ee.taltech.passman.integration;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import ee.taltech.passman.dto.derived.SetDerivedPasswordRequest;
import ee.taltech.passman.dto.login.LoginRequest;
import ee.taltech.passman.dto.master.SetMasterPasswordRequest;
import ee.taltech.passman.dto.registration.RegistrationRequest;
import ee.taltech.passman.entity.UserJwt;
import ee.taltech.passman.repository.DerivedKeyRepository;
import ee.taltech.passman.repository.RecoveryDataRepository;
import ee.taltech.passman.repository.UserJwtRepository;
import ee.taltech.passman.repository.UserRepository;
import jakarta.transaction.Transactional;
import java.util.ArrayList;
import org.junit.jupiter.api.AfterEach;
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
class PassmanControllerIntegrationTests {

  private static final String CONTENT_TYPE = "application/json";
  @Autowired private MockMvc mockMvc;
  @Autowired private ObjectMapper objectMapper;
  @Autowired private UserRepository userRepository;
  @Autowired private RecoveryDataRepository recoveryDataRepository;
  @Autowired private UserJwtRepository userJwtRepository;
  @Autowired private DerivedKeyRepository derivedKeyRepository;

  @Test
  @Transactional
  void
      givenRegistrationRequestAndLoginRequest_whenRegistrationRequestValidAndLoginRequestValid_thenReturnLoginResponseWithJwt()
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
        .containsOnlyOnce("Successfully registered");

    LoginRequest loginRequest =
        LoginRequest.builder().username(username).password(password).build();

    String loginRequestJson = objectMapper.writeValueAsString(loginRequest);

    MvcResult loginResult =
        mockMvc
            .perform(
                post("/api/login")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(loginRequestJson))
            .andDo(print())
            .andExpect(status().isOk())
            .andReturn();
    assertEquals(CONTENT_TYPE, loginResult.getResponse().getContentType());

    String responseContent = loginResult.getResponse().getContentAsString();

    String jwtToken = objectMapper.readTree(responseContent).get("jwtToken").asText();
    assertThat(jwtToken).isASCII().isNotEmpty().containsPattern(".");
  }

  @Test
  @Transactional
  void givenSetMasterPasswordRequest_whenUserLogsIntoVault_thenUserCanSetKeysForVault()
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
        .containsOnlyOnce("Successfully registered");

    LoginRequest loginRequest =
        LoginRequest.builder().username(username).password(password).build();

    String loginRequestJson = objectMapper.writeValueAsString(loginRequest);

    MvcResult loginResult =
        mockMvc
            .perform(
                post("/api/login")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(loginRequestJson))
            .andDo(print())
            .andExpect(status().isOk())
            .andReturn();
    assertEquals(CONTENT_TYPE, loginResult.getResponse().getContentType());

    SetMasterPasswordRequest setMasterPasswordRequest =
        SetMasterPasswordRequest.builder().password("ASecureAndUniqueMasterPassword").build();

    String passwordRequest = objectMapper.writeValueAsString(setMasterPasswordRequest);

    String jwtToken =
        objectMapper
            .readTree(loginResult.getResponse().getContentAsString())
            .get("jwtToken")
            .asText();
        mockMvc
            .perform(
                post("/api/setmaster")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(passwordRequest)
                    .header("Authorization", "Bearer " + jwtToken))
            .andDo(print())
            .andExpect(status().isCreated())
            .andReturn();

    MvcResult insertMasterResult = mockMvc
            .perform(
                    post("/api/insertmaster")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(passwordRequest)
                            .header("Authorization", "Bearer " + jwtToken))
            .andDo(print())
            .andExpect(status().isOk())
            .andReturn();

    jwtToken =
            objectMapper
                    .readTree(insertMasterResult.getResponse().getContentAsString())
                    .get("jwtToken")
                    .asText();

    String passwordIdentifier = "identifier";

    SetDerivedPasswordRequest setDerivedPasswordRequest =
            SetDerivedPasswordRequest.builder().identifier(passwordIdentifier).build();

    String derivedPasswordRequestJson = objectMapper.writeValueAsString(setDerivedPasswordRequest);

    mockMvc
            .perform(
                    post("/api/keys")
                            .header("Authorization", "Bearer " + jwtToken)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(derivedPasswordRequestJson))
            .andExpect(status().isCreated());

    MvcResult result =
            mockMvc
                    .perform(
                            get("/api/keys/" + passwordIdentifier).header("Authorization", "Bearer " + jwtToken))
                    .andExpect(status().isOk())
                    .andReturn();

    String responseContent = result.getResponse().getContentAsString();

    assertThat(responseContent).contains("\"password\"").hasSizeGreaterThan(50);
  }

  @Test
  @Transactional
  void givenRequestForSettingMasterPassword_whenRequestContainsValidJwtHeader_thenReturnStatus201()
      throws Exception {

    String username = "username";
    String password = "AValidAndStrongPassword";

    RegistrationRequest registrationRequest =
        RegistrationRequest.builder().username(username).password(password).build();

    String registrationRequestJson = objectMapper.writeValueAsString(registrationRequest);

    mockMvc.perform(
        post("/api/register")
            .contentType(MediaType.APPLICATION_JSON)
            .content(registrationRequestJson));

    LoginRequest loginRequest =
        LoginRequest.builder().username(username).password(password).build();

    String loginRequestJson = objectMapper.writeValueAsString(loginRequest);

    MvcResult loginResult =
        mockMvc
            .perform(
                post("/api/login")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(loginRequestJson))
            .andDo(print())
            .andExpect(status().isOk())
            .andReturn();

    SetMasterPasswordRequest setMasterPasswordRequest =
        SetMasterPasswordRequest.builder().password("ASecureAndUniqueMasterPassword").build();

    String passwordRequest = objectMapper.writeValueAsString(setMasterPasswordRequest);

    String jwtToken =
        objectMapper
            .readTree(loginResult.getResponse().getContentAsString())
            .get("jwtToken")
            .asText();

    MvcResult mvcResult =
        mockMvc
            .perform(
                post("/api/setmaster")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(passwordRequest)
                    .header("Authorization", "Bearer " + jwtToken))
            .andDo(print())
            .andReturn();

    assertThat(mvcResult.getResponse().getStatus()).isEqualTo(201);
  }

  @Test
  @Transactional
  void
      givenRequestForSettingMasterPassword_whenRequestContainsInvalidJwtHeader_thenReturnStatus403()
          throws Exception {

    String username = "username";
    String password = "AValidAndStrongPassword";

    RegistrationRequest registrationRequest =
        RegistrationRequest.builder().username(username).password(password).build();

    String registrationRequestJson = objectMapper.writeValueAsString(registrationRequest);

    mockMvc.perform(
        post("/api/register")
            .contentType(MediaType.APPLICATION_JSON)
            .content(registrationRequestJson));

    LoginRequest loginRequest =
        LoginRequest.builder().username(username).password(password).build();

    String loginRequestJson = objectMapper.writeValueAsString(loginRequest);

    mockMvc
        .perform(
            post("/api/login").contentType(MediaType.APPLICATION_JSON).content(loginRequestJson))
        .andDo(print())
        .andExpect(status().isOk())
        .andReturn();

    SetMasterPasswordRequest setMasterPasswordRequest =
        SetMasterPasswordRequest.builder().password("ASecureAndUniqueMasterPassword").build();

    String passwordRequest = objectMapper.writeValueAsString(setMasterPasswordRequest);

    MvcResult mvcResult =
        mockMvc
            .perform(
                post("/api/setmaster")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(passwordRequest)
                    .header("Authorization", "Bearer " + "NO JWT HERE!"))
            .andDo(print())
            .andReturn();

    assertThat(mvcResult.getResponse().getStatus()).isEqualTo(403);
  }

  @Test
  @Transactional
  void
      givenDuplicateRequestsForRegisteringAccountWithSameUsername_whenSecondRequestHandled_thenReturnStatus400WithUsernameAlreadyExistsMessage()
          throws Exception {

    String username = "username";
    String password = "AValidAndStrongPassword";

    RegistrationRequest registrationRequest =
        RegistrationRequest.builder().username(username).password(password).build();

    String registrationRequestJson = objectMapper.writeValueAsString(registrationRequest);

    mockMvc.perform(
        post("/api/register")
            .contentType(MediaType.APPLICATION_JSON)
            .content(registrationRequestJson));

    MvcResult result =
        mockMvc
            .perform(
                post("/api/register")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(registrationRequestJson))
            .andExpect(status().isBadRequest())
            .andReturn();

    assertThat(result.getResponse().getContentAsString())
        .containsOnlyOnce("Account with username already exists");
  }

  @AfterEach
  public void cleanup() {
    userJwtRepository.deleteAll();
    derivedKeyRepository.deleteAll();
    userRepository.deleteAll();
  }
}
