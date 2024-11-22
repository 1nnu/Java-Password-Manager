package ee.taltech.passman.repository;

import ee.taltech.passman.entity.DerivedUserKey;
import ee.taltech.passman.entity.User;
import java.util.List;
import org.springframework.data.repository.CrudRepository;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Repository;

@Repository
public interface DerivedKeyRepository extends CrudRepository<DerivedUserKey, Long> {
  List<DerivedUserKey> findByUser(User user);

  DerivedUserKey findByDerivedKeyNameAndUser(@NonNull String derivedKeyName, @NonNull User user);

  long deleteByUserAndDerivedKeyName(@NonNull User user, @NonNull String derivedKeyName);
}
