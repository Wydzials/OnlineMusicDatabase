package pl.wydzials.onlinemusicdatabase.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import pl.wydzials.onlinemusicdatabase.model.RateableEntity;

@Repository
public interface RateableEntityRepository extends JpaRepository<RateableEntity, Long> {
}
