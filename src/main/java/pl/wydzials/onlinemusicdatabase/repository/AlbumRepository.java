package pl.wydzials.onlinemusicdatabase.repository;

import java.util.List;
import java.util.Optional;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import pl.wydzials.onlinemusicdatabase.model.Album;

@Repository
public interface AlbumRepository extends JpaRepository<Album, Long> {

  @EntityGraph(attributePaths = {"recordings"})
  Optional<Album> findAlbumById(Long id);

  @Query("select a from Album a "
      + "where lower(a.name) like %:query% "
      + "order by a.numberOfRatings desc")
  List<Album> search(String query, Pageable pageable);

  @Query("select a from Album a "
      + "where a.numberOfRatings >= :minRatings "
      + "and a.numberOfRatings <= :maxRatings "
      + "and a.year >= :minYear "
      + "and a.year <= :maxYear "
      + "order by a.averageRating desc")
  List<Album> findTopAlbums(final int minRatings, final int maxRatings, final int minYear, final int maxYear,
      Pageable pageable);
}
