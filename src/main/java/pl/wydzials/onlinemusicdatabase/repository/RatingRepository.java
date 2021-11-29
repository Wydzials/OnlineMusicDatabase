package pl.wydzials.onlinemusicdatabase.repository;

import java.util.List;
import java.util.Optional;
import java.util.Set;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import pl.wydzials.onlinemusicdatabase.model.RateableEntity;
import pl.wydzials.onlinemusicdatabase.model.Rating;
import pl.wydzials.onlinemusicdatabase.model.User;

public interface RatingRepository extends JpaRepository<Rating, Long> {

  @Query("select r from Rating r where r.user.username = :username and r.entity in :entities")
  List<Rating> findByUsernameAndEntities(String username, Set<? extends RateableEntity> entities);

  @Query("select r from Rating r where r.user = :user and r.entity = :entity")
  Optional<Rating> findByUserAndEntity(User user, RateableEntity entity);
}
