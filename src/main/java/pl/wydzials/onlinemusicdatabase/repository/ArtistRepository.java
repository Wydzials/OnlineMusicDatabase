package pl.wydzials.onlinemusicdatabase.repository;

import java.util.List;
import java.util.Set;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import pl.wydzials.onlinemusicdatabase.model.Artist;
import pl.wydzials.onlinemusicdatabase.model.Artist.ArtistType;
import pl.wydzials.onlinemusicdatabase.model.Artist.Genre;

@Repository
public interface ArtistRepository extends JpaRepository<Artist, Long> {

  @Query("select a from Artist a "
      + "where lower(a.name) like %:query% "
      + "order by a.numberOfRatings desc")
  List<Artist> search(String query, Pageable pageable);

  @Query("select a from Artist a "
      + "where a.numberOfRatings >= :minRatings "
      + "and a.numberOfRatings <= :maxRatings "
      + "and a.artistType in :artistTypes "
      + "and a.genre in :genres "
      + "order by a.averageRating desc")
  List<Artist> findTopArtists(final Set<ArtistType> artistTypes, final Set<Genre> genres,
      final int minRatings, final int maxRatings, Pageable pageable);
}
