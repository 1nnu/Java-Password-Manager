package ee.taltech.passman.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter
@Setter
@ToString
@RequiredArgsConstructor
@Builder
@AllArgsConstructor
@Table(name = "USERS")
public class User {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(name = "id", nullable = false)
  private Long id;

  @Column(name = "master_password_hash")
  private String masterPasswordHash;

  @Column(name = "username", nullable = false, unique = true)
  private String username;

  @Column(name = "encoded_password", nullable = false)
  private String encodedPassword;

}
