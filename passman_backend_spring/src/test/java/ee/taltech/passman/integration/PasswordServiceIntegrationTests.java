package ee.taltech.passman.integration;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import ee.taltech.passman.dto.derived.SetDerivedPasswordRequest;
import ee.taltech.passman.dto.master.InsertMasterPasswordRequest;
import ee.taltech.passman.dto.master.SetMasterPasswordRequest;
import ee.taltech.passman.entity.DerivedUserKey;
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
class PasswordServiceIntegrationTests {

  private static final String CONTENT_TYPE = "application/json";
  @Autowired private MockMvc mockMvc;
  @Autowired private ObjectMapper objectMapper;
  @Autowired private UserRepository userRepository;
  @Autowired private UserJwtRepository userJwtRepository;
  @Autowired private DerivedKeyRepository derivedKeyRepository;
  @Autowired private RecoveryDataRepository recoveryDataRepository;
  @Autowired private JwtUtil jwtUtil;

  private String userJwt;
  private User user;

  @BeforeEach
  public void setup() {
    user =
        User.builder()
            .username("user")
            .encodedPassword("accountPassword")
            .masterPasswordHash("MasterPassword")
            .build();
    userRepository.save(user);
    userJwt = jwtUtil.generateJwtToken("user");
  }

  @Test
  void
      givenRequestForCreatingDerivedPassword_whenRequestIsValid_thenCreateADerivedPasswordAndStoreItInDbAndReturnStatusCode201()
          throws Exception {
    String masterPassword = "SecureP@ssword123";

    SetMasterPasswordRequest setMasterPasswordRequest = new SetMasterPasswordRequest();
    setMasterPasswordRequest.setPassword(masterPassword);

    String setMasterPasswordRequestJson = objectMapper.writeValueAsString(setMasterPasswordRequest);

    mockMvc
            .perform(
                    post("/api/setmaster")
                            .header("Authorization", "Bearer " + userJwt)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(setMasterPasswordRequestJson))
            .andExpect(status().isCreated());

    InsertMasterPasswordRequest insertMasterPasswordRequest = new InsertMasterPasswordRequest();
    insertMasterPasswordRequest.setPassword(masterPassword);

    String insertMasterPasswordRequestJson =
            objectMapper.writeValueAsString(insertMasterPasswordRequest);

    MvcResult mvcResult =
            mockMvc
                    .perform(
                            post("/api/insertmaster")
                                    .header("Authorization", "Bearer " + userJwt)
                                    .contentType(MediaType.APPLICATION_JSON)
                                    .content(setMasterPasswordRequestJson))
                    .andExpect(status().isOk())
                    .andReturn();

    String responseString = mvcResult.getResponse().getContentAsString();
    String vaultJwtToken = objectMapper.readTree(responseString).get("jwtToken").asText();

    SetDerivedPasswordRequest setDerivedPasswordRequest =
        SetDerivedPasswordRequest.builder().identifier("identifier").build();

    String derivedPasswordRequestJson = objectMapper.writeValueAsString(setDerivedPasswordRequest);

    MvcResult setDerivedPasswordResult =
        mockMvc
            .perform(
                post("/api/keys")
                    .header("Authorization", "Bearer " + vaultJwtToken)
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(derivedPasswordRequestJson))
            .andDo(print())
            .andExpect(status().isCreated())
            .andReturn();
    assertEquals(CONTENT_TYPE, setDerivedPasswordResult.getResponse().getContentType());
    assertThat(setDerivedPasswordResult.getResponse().getContentAsString())
        .containsOnlyOnce("Generated password successfully");

    DerivedUserKey userKey = derivedKeyRepository.findByUser(user).getFirst();
    assertThat(userKey.getDerivedKeyName()).isEqualTo("identifier");
    assertThat(userKey.getDerivedKey()).isASCII().hasSizeGreaterThanOrEqualTo(20);
  }

  @Test
  void
      givenRequestForGettingAllDerivedPasswordIdentifiers_whenRequestIsValid_thenReturnArrayOfDerivedPasswordIdentifiersAndStatusCode200()
          throws Exception {

    String masterPassword = "SecureP@ssword123";

    SetMasterPasswordRequest setMasterPasswordRequest = new SetMasterPasswordRequest();
    setMasterPasswordRequest.setPassword(masterPassword);

    String setMasterPasswordRequestJson = objectMapper.writeValueAsString(setMasterPasswordRequest);

    mockMvc
            .perform(
                    post("/api/setmaster")
                            .header("Authorization", "Bearer " + userJwt)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(setMasterPasswordRequestJson))
            .andExpect(status().isCreated());

    InsertMasterPasswordRequest insertMasterPasswordRequest = new InsertMasterPasswordRequest();
    insertMasterPasswordRequest.setPassword(masterPassword);

    String insertMasterPasswordRequestJson =
            objectMapper.writeValueAsString(insertMasterPasswordRequest);

    MvcResult mvcResult =
            mockMvc
                    .perform(
                            post("/api/insertmaster")
                                    .header("Authorization", "Bearer " + userJwt)
                                    .contentType(MediaType.APPLICATION_JSON)
                                    .content(setMasterPasswordRequestJson))
                    .andExpect(status().isOk())
                    .andReturn();

    String responseString = mvcResult.getResponse().getContentAsString();
    String vaultJwtToken = objectMapper.readTree(responseString).get("jwtToken").asText();


    SetDerivedPasswordRequest setDerivedPasswordRequest =
        SetDerivedPasswordRequest.builder().identifier("identifier").build();

    String derivedPasswordRequestJson = objectMapper.writeValueAsString(setDerivedPasswordRequest);

    mockMvc
        .perform(
            post("/api/keys")
                .header("Authorization", "Bearer " + vaultJwtToken)
                .contentType(MediaType.APPLICATION_JSON)
                .content(derivedPasswordRequestJson))
        .andExpect(status().isCreated());

    setDerivedPasswordRequest =
        SetDerivedPasswordRequest.builder().identifier("identifier2").build();

    derivedPasswordRequestJson = objectMapper.writeValueAsString(setDerivedPasswordRequest);

    mockMvc
        .perform(
            post("/api/keys")
                .header("Authorization", "Bearer " + vaultJwtToken)
                .contentType(MediaType.APPLICATION_JSON)
                .content(derivedPasswordRequestJson))
        .andExpect(status().isCreated());

    MvcResult mvcResultIds =
        mockMvc
            .perform(
                get("/api/keys")
                    .header("Authorization", "Bearer " + vaultJwtToken)
                    .contentType(MediaType.APPLICATION_JSON))
            .andDo(print())
            .andExpect(status().isOk())
            .andReturn();

    String responseContent = mvcResultIds.getResponse().getContentAsString();

    assertThat(responseContent).contains("\"identifier\"").contains("\"identifier2\"");
  }

  @Test
  void
      givenRequestForGettingSpecificPasswordByIdentifier_whenRequestIsValid_thenReturnJsonWithPasswordAndStatusCode200()
          throws Exception {

    String masterPassword = "SecureP@ssword123";

    SetMasterPasswordRequest setMasterPasswordRequest = new SetMasterPasswordRequest();
    setMasterPasswordRequest.setPassword(masterPassword);

    String setMasterPasswordRequestJson = objectMapper.writeValueAsString(setMasterPasswordRequest);

    mockMvc
        .perform(
            post("/api/setmaster")
                .header("Authorization", "Bearer " + userJwt)
                .contentType(MediaType.APPLICATION_JSON)
                .content(setMasterPasswordRequestJson))
        .andExpect(status().isCreated());

    InsertMasterPasswordRequest insertMasterPasswordRequest = new InsertMasterPasswordRequest();
    insertMasterPasswordRequest.setPassword(masterPassword);

    String insertMasterPasswordRequestJson =
        objectMapper.writeValueAsString(insertMasterPasswordRequest);

    MvcResult mvcResult =
        mockMvc
            .perform(
                post("/api/insertmaster")
                    .header("Authorization", "Bearer " + userJwt)
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(setMasterPasswordRequestJson))
            .andExpect(status().isOk())
            .andReturn();

    String responseString = mvcResult.getResponse().getContentAsString();
    String vaultJwtToken = objectMapper.readTree(responseString).get("jwtToken").asText();


    String passwordIdentifier = "identifier";

    SetDerivedPasswordRequest setDerivedPasswordRequest =
        SetDerivedPasswordRequest.builder().identifier(passwordIdentifier).build();

    String derivedPasswordRequestJson = objectMapper.writeValueAsString(setDerivedPasswordRequest);

    mockMvc
        .perform(
            post("/api/keys")
                .header("Authorization", "Bearer " + vaultJwtToken)
                .contentType(MediaType.APPLICATION_JSON)
                .content(derivedPasswordRequestJson))
        .andExpect(status().isCreated());

    MvcResult result =
        mockMvc
            .perform(
                get("/api/keys/" + passwordIdentifier).header("Authorization", "Bearer " + vaultJwtToken))
            .andExpect(status().isOk())
            .andReturn();

    String responseContent = result.getResponse().getContentAsString();

    assertThat(responseContent).contains("\"password\"").hasSizeGreaterThan(50);
  }

  @Test
  void
      givenRequestForDeletingSpecificPasswordByIdentifier_whenRequestIsValid_thenReturnStatusCode204()
          throws Exception {

    String masterPassword = "SecureP@ssword123";

    SetMasterPasswordRequest setMasterPasswordRequest = new SetMasterPasswordRequest();
    setMasterPasswordRequest.setPassword(masterPassword);

    String setMasterPasswordRequestJson = objectMapper.writeValueAsString(setMasterPasswordRequest);

    mockMvc
            .perform(
                    post("/api/setmaster")
                            .header("Authorization", "Bearer " + userJwt)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(setMasterPasswordRequestJson))
            .andExpect(status().isCreated());

    InsertMasterPasswordRequest insertMasterPasswordRequest = new InsertMasterPasswordRequest();
    insertMasterPasswordRequest.setPassword(masterPassword);

    String insertMasterPasswordRequestJson =
            objectMapper.writeValueAsString(insertMasterPasswordRequest);

    MvcResult mvcResult =
            mockMvc
                    .perform(
                            post("/api/insertmaster")
                                    .header("Authorization", "Bearer " + userJwt)
                                    .contentType(MediaType.APPLICATION_JSON)
                                    .content(setMasterPasswordRequestJson))
                    .andExpect(status().isOk())
                    .andReturn();

    String responseString = mvcResult.getResponse().getContentAsString();
    String vaultJwtToken = objectMapper.readTree(responseString).get("jwtToken").asText();

    String passwordIdentifier = "identifier";

    SetDerivedPasswordRequest setDerivedPasswordRequest =
        SetDerivedPasswordRequest.builder().identifier(passwordIdentifier).build();

    String derivedPasswordRequestJson = objectMapper.writeValueAsString(setDerivedPasswordRequest);

    mockMvc
        .perform(
            post("/api/keys")
                .header("Authorization", "Bearer " + vaultJwtToken)
                .contentType(MediaType.APPLICATION_JSON)
                .content(derivedPasswordRequestJson))
        .andExpect(status().isCreated());

    MvcResult result =
        mockMvc
            .perform(
                delete("/api/keys/" + passwordIdentifier)
                    .header("Authorization", "Bearer " + vaultJwtToken))
            .andExpect(status().isNoContent())
            .andReturn();
  }

  @AfterEach
  public void cleanup() {
    recoveryDataRepository.deleteAll();
    userJwtRepository.deleteAll();
    derivedKeyRepository.deleteAll();
    userRepository.deleteAll();
  }
}
