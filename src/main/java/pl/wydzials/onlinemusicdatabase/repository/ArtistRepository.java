package pl.wydzials.onlinemusicdatabase.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import pl.wydzials.onlinemusicdatabase.model.Artist;

@Repository
public interface ArtistRepository extends JpaRepository<Artist, Long> {
}
