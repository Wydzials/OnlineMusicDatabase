package pl.wydzials.onlinemusicdatabase.repository;

import java.util.List;
import java.util.Optional;
import java.util.Set;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import pl.wydzials.onlinemusicdatabase.model.Artist;
import pl.wydzials.onlinemusicdatabase.model.Artist.Genre;
import pl.wydzials.onlinemusicdatabase.model.RateableEntity;
import pl.wydzials.onlinemusicdatabase.model.Rating;
import pl.wydzials.onlinemusicdatabase.model.Rating.Stars;
import pl.wydzials.onlinemusicdatabase.model.User;

public interface RatingRepository extends JpaRepository<Rating, Long> {

  @Query("select r from Rating r where r.user.username = :username and r.entity in :entities")
  List<Rating> findByUsernameAndEntities(String username, Set<? extends RateableEntity> entities);

  @Query("select r from Rating r where r.user = :user and r.entity = :entity")
  Optional<Rating> findByUserAndEntity(User user, RateableEntity entity);

  @Query("select r from Rating r where r.user = :user "
      + "and r.entityClass = :entityClass "
      + "order by r.created desc")
  List<Rating> findByUserOrderByDateDesc(User user, Class<? extends RateableEntity> entityClass, Pageable pageable);

  @Query("select count(r) from Rating r where r.user = :user "
      + "and r.entityClass = :entityClass ")
  long countRatingsByUser(User user, Class<? extends RateableEntity> entityClass);

  @Query("select count(r) from Rating r where r.user = :user")
  long countRatingsByUser(User user);

  @Query("select avg(r.stars) from Rating r where r.user = :user")
  Double averateRatingByUser(User user);

  @Query("select r.stars as stars, count(r.stars) as count from Rating r "
      + "where r.user = :user "
      + "group by r.stars")
  List<RatingsCountByStars> countUserRatingsGroupByStars(User user);

  @Query("select a.genre as genre, count(r.stars) as count from Rating r, Artist a "
      + "where r.user = :user "
      + "and r.entity.id = a.id "
      + "group by a.genre "
      + "order by count(r.stars) desc")
  List<RatingsCountByGenre> countUserRatingsGroupByArtistGenre(User user);

  @Query("select rec.artist as artist, count(r.stars) as count from Rating r, Recording rec "
      + "where r.user = :user "
      + "and r.entity.id = rec.id "
      + "group by rec.artist "
      + "order by count(r.stars) desc")
  List<RatingsCountByArtist> countUserRatingsGroupByArtist(User user, Pageable pageable);

  interface RatingsCountByStars {

    Stars getStars();

    Integer getCount();
  }

  interface RatingsCountByGenre {

    Genre getGenre();

    Integer getCount();
  }

  interface RatingsCountByArtist {

    Artist getArtist();

    Integer getCount();
  }
}
