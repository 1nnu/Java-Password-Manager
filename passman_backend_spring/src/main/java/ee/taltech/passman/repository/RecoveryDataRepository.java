package ee.taltech.passman.repository;

import ee.taltech.passman.entity.RecoveryData;
import ee.taltech.passman.entity.User;
import org.springframework.data.repository.CrudRepository;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Repository;

@Repository
public interface RecoveryDataRepository extends CrudRepository<RecoveryData, Long> {
  RecoveryData findByUser(@NonNull User user);
}
