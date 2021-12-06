package pl.wydzials.onlinemusicdatabase.repository;

import java.util.List;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import pl.wydzials.onlinemusicdatabase.model.Artist;

@Repository
public interface ArtistRepository extends JpaRepository<Artist, Long>, ArtistRepositoryCustom {

  @Query("select a from Artist a "
      + "where lower(a.name) like %:query% "
      + "order by a.numberOfRatings desc")
  List<Artist> search(String query, Pageable pageable);
}
