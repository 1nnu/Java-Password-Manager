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
@Table(name = "USERS_JWT")
public class UserJwt {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Long id;

    @ToString.Exclude
    @OneToOne
    @JoinColumn(name = "user_id")
    private User user;

    @Column(name = "jwt")
    private String jwt;

}
