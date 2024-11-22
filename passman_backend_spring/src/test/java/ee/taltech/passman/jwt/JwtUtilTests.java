package ee.taltech.passman.jwt;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

import ee.taltech.passman.security.jwt.JwtUtil;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

class JwtUtilTests {

  private static JwtUtil jwtUtil;

  private static String validJwt;

  @BeforeAll
  static void init() {
    jwtUtil = new JwtUtil();
    validJwt = jwtUtil.generateJwtToken("username");
  }

  @Test
  void givenRequestForGeneratingJwtForUser_whenMethodExecutes_thenReturnValidJwt() {
    String username = "username";

    String jwt = jwtUtil.generateJwtToken(username);

    assertThat(jwt).isNotEmpty().isASCII().containsAnyOf(".");
  }

  @Test
  void givenAValidJwtAndSubject_whenMethodExecutes_thenValidateJwtAndReturnTrue() {
    String username = "username";

    assertEquals(jwtUtil.getUsernameFromToken(validJwt), username);
  }
}
