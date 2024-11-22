package ee.taltech.passman.security.services;

import ee.taltech.passman.entity.User;
import ee.taltech.passman.exceptions.ValidationException;
import ee.taltech.passman.repository.UserRepository;
import java.util.Arrays;
import java.util.Collections;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class UserDetailsServiceImpl implements UserDetailsService {
  private final UserRepository userRepository;

  public UserDetailsServiceImpl(UserRepository userRepository) {
    this.userRepository = userRepository;}

  @Override
  public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
    User user = userRepository.findByUsername(username);
    if (user == null) {
      throw new ValidationException("User with given username does not exist");
    }


    return new org.springframework.security.core.userdetails.User(
        user.getUsername(),
        user.getEncodedPassword(),
        true,
        true,
        true,
        true,
        Collections.singleton(new SimpleGrantedAuthority("Authenticated")));
  }

  public UserDetails loadUserAndGiveVaultPermissions(String username)
      throws UsernameNotFoundException {
    User user = userRepository.findByUsername(username);
    if (user == null) {
      throw new ValidationException("User with given username does not exist");
    }

    return new org.springframework.security.core.userdetails.User(
        user.getUsername(),
        user.getEncodedPassword(),
        true,
        true,
        true,
        true,
        Arrays.asList(
            new SimpleGrantedAuthority("Authenticated"),
            new SimpleGrantedAuthority("vaultaccess")));
  }
}
