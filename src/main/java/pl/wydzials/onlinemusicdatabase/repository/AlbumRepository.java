package pl.wydzials.onlinemusicdatabase.repository;

import java.util.Optional;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import pl.wydzials.onlinemusicdatabase.model.Album;

@Repository
public interface AlbumRepository extends JpaRepository<Album, Long> {

  @EntityGraph(attributePaths = {"recordings"})
  Optional<Album> findAlbumById(Long id);
}
