package ee.taltech.passman.repository;

import ee.taltech.passman.entity.User;
import ee.taltech.passman.entity.UserJwt;
import org.springframework.data.repository.CrudRepository;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Repository;

@Repository
public interface UserJwtRepository extends CrudRepository<UserJwt, Long> {
  UserJwt findByUser(@NonNull User user);

  long deleteByUser(@NonNull User user);
}
