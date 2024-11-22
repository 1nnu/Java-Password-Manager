package ee.taltech.passman.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Entity
@Getter
@Setter
@ToString
@RequiredArgsConstructor
@Table(name = "DERIVED_USER_KEYS")
public class DerivedUserKey {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(name = "id", nullable = false)
  private Long id;

  @Column(name = "derived_key", nullable = false)
  private String derivedKey;

  @Column(name = "derived_key_name", unique = true)
  private String derivedKeyName;

  @ToString.Exclude
  @ManyToOne(optional = false)
  @JoinColumn(name = "user_id", nullable = false)
  private User user;
}
