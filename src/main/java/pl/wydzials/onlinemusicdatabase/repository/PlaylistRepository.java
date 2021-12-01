package pl.wydzials.onlinemusicdatabase.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import pl.wydzials.onlinemusicdatabase.model.Playlist;

public interface PlaylistRepository extends JpaRepository<Playlist, Long> {
}
