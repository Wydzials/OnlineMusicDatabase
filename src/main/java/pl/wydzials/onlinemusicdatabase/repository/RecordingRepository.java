package pl.wydzials.onlinemusicdatabase.repository;

import java.util.List;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import pl.wydzials.onlinemusicdatabase.model.Recording;

@Repository
public interface RecordingRepository extends JpaRepository<Recording, Long> {

  @Query("select r from Recording r "
      + "where lower(r.title) like %:query% "
      + "order by r.numberOfRatings desc")
  List<Recording> search(String query, Pageable pageable);

  @Query("select r from Recording r "
      + "where r.numberOfRatings >= :minRatings "
      + "and r.numberOfRatings <= :maxRatings "
      + "order by r.averageRating desc")
  List<Recording> findTopRecordings(final int minRatings, final int maxRatings, Pageable pageable);
}
