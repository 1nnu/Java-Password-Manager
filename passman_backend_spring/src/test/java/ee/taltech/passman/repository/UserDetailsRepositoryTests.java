package ee.taltech.passman.repository;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import ee.taltech.passman.entity.User;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

@DataJpaTest
class UserDetailsRepositoryTests {

  @Autowired private UserRepository userRepository;

  private User testUser;

  @BeforeEach
  public void setUp() {
    testUser = new User();
    testUser.setUsername("someusername");
    testUser.setEncodedPassword("$2a$10$ZLhnHxdpHETcxmtEStgpI./Ri1mksgJ9iDP36FmfMdYyVg9g0b2dq");
    userRepository.save(testUser);
  }

  @AfterEach
  public void tearDown() {
    userRepository.delete(testUser);
  }

  @Test
  void givenUsername_whenUserInDatabase_thenCanBeFoundByUsername() {

    User userDetails = userRepository.findByUsername(testUser.getUsername());

    assertNotNull(userDetails);
    assertThat(userDetails).isEqualTo(testUser);
  }

  @Test
  void givenUsername_whenUserInDatabase_thenCanIdCanBeFoundByUsername() {

    User user = userRepository.findByUsername(testUser.getUsername());

    assertNotNull(user);
    assertThat(user.getId()).isPositive();
  }
}
