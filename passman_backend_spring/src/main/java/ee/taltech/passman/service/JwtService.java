package ee.taltech.passman.service;

import ee.taltech.passman.entity.User;
import ee.taltech.passman.entity.UserJwt;
import ee.taltech.passman.repository.UserJwtRepository;
import ee.taltech.passman.security.jwt.JwtUtil;
import org.springframework.stereotype.Service;

@Service
public class JwtService {

  private final UserJwtRepository jwtRepository;
  private final JwtUtil jwtUtil;

  public JwtService(UserJwtRepository jwtRepository, JwtUtil jwtUtil) {
    this.jwtRepository = jwtRepository;
    this.jwtUtil = jwtUtil;
  }

  public String createJwtForUser(User user) {
    String newJwt = jwtUtil.generateJwtToken(user.getUsername());
    UserJwt userJwt = new UserJwt();
    userJwt.setJwt(newJwt);
    userJwt.setUser(user);
    jwtRepository.save(userJwt);
    return newJwt;
  }



  public String createVaultAccessJwtForUser(User user) {
    String newJwt = jwtUtil.generateVaultAccessJwtToken(user.getUsername());
    UserJwt userJwt = new UserJwt();
    userJwt.setJwt(newJwt);
    userJwt.setUser(user);
    jwtRepository.save(userJwt);
    return newJwt;
  }
}
